import React, { useState, useEffect, useRef } from "react";
import { Card, Form, Button, InputGroup, Image, CloseButton } from "react-bootstrap";
import { useAuth } from "../../../Context/AuthContext";
import { connectWs, disconnectWs, subscribeTicket, sendTicketMessage } from "../../../Api/Services/wsClient";
import { fetchHistory,uploadChatImage } from "../../../Api/Services/ChatServices"; 
import { Modal } from "react-bootstrap";
import '../../../index.css';


const ChatRoom = ({ ticketId, onSystemMessage }) => {
  const token = localStorage.getItem("token");
  const { user } = useAuth();
  const [viewerOpen, setViewerOpen] = useState(false);
  const [viewerImage, setViewerImage] = useState(null);


  const CURRENT_USER_ROLE = (user?.role || "").toLowerCase(); 

  const [messages, setMessages] = useState([]);      
  const [newMessage, setNewMessage] = useState("");  

  const subRef = useRef(null);
  const messagesEndRef = useRef(null);


  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const fileInputRef = useRef(null);


  const closeViewer = () => {
  setViewerOpen(false);
  setViewerImage(null);
  };

  const normalize = (m) => {
    const sender = (m.senderRole || "system").toLowerCase();
    const isSystem = m.type === "SYSTEM" || sender === "system";

    // const base = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
    const origin = window.location.origin;

    const img = m.type === "IMAGE"? (m.content?.startsWith("http") ? m.content : `${origin}${m.content}`): null;

    return {
      id: m.id || Date.now(),
      sender: isSystem ? "system" : sender,
      text: m.type === "IMAGE" ? "" : (m.content || ""),
      image: img,
      time: m.createdAt ? new Date(m.createdAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) : "",
    };
  };

  // scroll
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);


  //  history and connect ws subscribe
  useEffect(() => {
    if (!ticketId || !token) return;

    let mounted = true;

    const run = async () => {
      // 1) fetch history
      const history = await fetchHistory(ticketId);
      if (!mounted) return;
      setMessages(history.map(normalize));

      // 2) WS connect + subscribe
      connectWs({
        token,
        onConnect: () => {
          subRef.current = subscribeTicket(ticketId, (incoming) => {
            setMessages((prev) => [...prev, normalize(incoming)]);
            if(incoming?.type === "SYSTEM") onSystemMessage?.();
            
          });
        },
        onError: (e) => console.error("WS error", e),
      });
    };

    run();

    return () => {
      mounted = false;
      try { subRef.current?.unsubscribe(); } catch {}
      disconnectWs();
    };
  }, [ticketId, token,onSystemMessage]);

  // file select
  const handleFileSelect = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setSelectedFile(file);

    if (file.type.startsWith("image/")) {
      const url = URL.createObjectURL(file);
      setPreviewUrl(url);
    }
  };

  const clearSelectedFile = () => {
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setSelectedFile(null);
    setPreviewUrl(null);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const triggerFileInput = () => {
    fileInputRef.current?.click();
  };

  // handlesend
  const handleSend = async (e) => {
    e.preventDefault();
    if(selectedFile){
      try{
          const fullUrl = await uploadChatImage(ticketId, selectedFile);
          sendTicketMessage(ticketId, { content: fullUrl, chatMessageType: "IMAGE" });
          clearSelectedFile();
      }catch(error){
          console.error("Upload file fail", error);
          alert("Upload file fail");
          return;
      }
      if(!newMessage.trim()) return;
    }

    if (!newMessage.trim()) return;

    sendTicketMessage(ticketId, {
      content: newMessage.trim(),
      chatMessageType: "CHAT",
    });

    setNewMessage("");
    clearSelectedFile();
  };

  return (
    <Card className="shadow-sm border-0 h-100">
      <Card.Header className="bg-white py-3 border-bottom">
        <div className="d-flex align-items-center justify-content-between gap-2">
          <strong>Live Chat</strong>
          <span className="badge bg-success rounded-pill" style={{ fontSize: "0.7rem" }}>
            Online
          </span>
        </div>
      </Card.Header>

      <Card.Body className="d-flex flex-column p-3 bg-light" style={{ height: "400px", overflowY: "auto" }}>
        {messages.map((msg) => {
          const isMe = msg.sender === CURRENT_USER_ROLE;
          const isSystem = msg.sender === "system";

          if (isSystem) {
            return (
              <div key={msg.id} className="text-center my-2">
                <small className="text-muted bg-white px-2 py-1 rounded border">{msg.text}</small>
              </div>
            );
          }

          return (
            <div key={msg.id} className={`d-flex mb-3 ${isMe ? "justify-content-end" : "justify-content-start"}`}>
              <div
                className={`p-2 rounded-3 shadow-sm ${isMe ? "bg-primary text-white" : "bg-white text-dark border"}`}
                style={{
                  maxWidth: "75%",
                  borderBottomRightRadius: isMe ? "0" : "1rem",
                  borderBottomLeftRadius: isMe ? "1rem" : "0",
                }}
              >
                {msg.image && (
                  <div className="mb-2">
                    <Image
                      src={msg.image}
                      alt="attachment"
                      fluid
                      rounded
                      className="border"
                      style={{ maxHeight: "200px", objectFit: "cover" , cursor: "pointer"}}
                      onClick={()=> {setViewerImage(msg.image); setViewerOpen(true);}}
                    />
                  </div>
                )}

                {msg.text && <div className="mb-1" style={{ fontSize: "0.9rem" }}>{msg.text}</div>}

                <div className={`text-end small ${isMe ? "text-white-50" : "text-muted"}`} style={{ fontSize: "0.7rem" }}>
                  {msg.time}
                </div>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </Card.Body>

      <Card.Footer className="bg-white py-3 border-top-0">
        {previewUrl && (
          <div className="mb-2 position-relative d-inline-block">
            <Image src={previewUrl} alt="Preview" thumbnail style={{ height: "80px", width: "auto" }} />
            <CloseButton
              onClick={clearSelectedFile}
              className="position-absolute top-0 start-100 translate-middle bg-white shadow-sm p-1"
              style={{ fontSize: "0.7rem" }}
            />
          </div>
        )}

        <Form onSubmit={handleSend}>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            accept="image/png, image/jpeg, image/jpg"
            style={{ display: "none" }}
          />

          <InputGroup>
            <Button variant="outline-secondary" onClick={triggerFileInput} title="Attach File">
              <span style={{ fontSize: "1.2rem" }}>File</span>
            </Button>

            <Form.Control
              placeholder="Please Enter message...."
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              className="border-secondary"
            />

            <Button variant="primary" type="submit" disabled={!newMessage.trim() && !selectedFile}>
              Send
            </Button>
          </InputGroup>
        </Form>
      </Card.Footer>

      <Modal show={viewerOpen} onHide={closeViewer} centered backdrop="static" keyboard dialogClassName="image-viewer-dialog" contentClassName="image-viewer-content">
        <Modal.Body className="p-2 d-flex flex-column align-items-center bg-dark">
          {viewerImage && (
          <>
          <Image src={viewerImage} alt="Full view"  onClick={closeViewer}  style={{ maxHeight: "80vh", maxWidth: "90vw",objectFit: "contain" , cursor:"zoom-out"}}
          />
          <a href={viewerImage} download  target="_blank" className="btn btn-light btn-sm mt-2">
          Download
          </a>
          </>
        )}
        </Modal.Body>
      </Modal>
    </Card>


  );
};

export default ChatRoom;
