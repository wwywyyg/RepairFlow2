import React, { useState, useEffect, useRef } from 'react';
import { Card, Form, Button, InputGroup, Image, CloseButton } from 'react-bootstrap'; // 新增 Image, CloseButton

const ChatRoom = ({ ticketId }) => {
  const CURRENT_USER_ROLE = "employee";

  // 1. 模拟消息数据 (新增了一个包含图片的消息例子)
  const [messages, setMessages] = useState([
    { id: 1, sender: "customer", text: "你好，我的屏幕摔碎了，大概要修多久？", time: "10:00 AM" },
    // 👇 模拟客户发送的图片消息
    { id: 10, sender: "customer", text: "", image: "https://placehold.co/300x200?text=Broken+Screen+Img", time: "10:02 AM" },
    { id: 2, sender: "employee", text: "您好！收到照片了。通常收到设备后 24 小时内可以修好。", time: "10:05 AM" },
    { id: 4, sender: "system", text: "系统通知：状态更新为 [DEVICE_RECEIVED]", time: "10:12 AM" },
  ]);

  const [newMessage, setNewMessage] = useState("");
  const messagesEndRef = useRef(null);
  
  // --- 新增状态和 Ref 用于文件上传 ---
  const [selectedFile, setSelectedFile] = useState(null); // 存储选中的文件对象
  const [previewUrl, setPreviewUrl] = useState(null);     // 存储图片预览 URL
  const fileInputRef = useRef(null);                      // 用于触发隐藏的 file input

  // 自动滚动
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);


  // --- 新增：处理文件选择 ---
  const handleFileSelect = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      // 如果是图片，创建预览 URL
      if (file.type.startsWith('image/')) {
        const url = URL.createObjectURL(file);
        setPreviewUrl(url);
      }
    }
  };

  // --- 新增：清除选中的文件 ---
  const clearSelectedFile = () => {
    if (previewUrl) URL.revokeObjectURL(previewUrl); // 释放内存
    setSelectedFile(null);
    setPreviewUrl(null);
    if (fileInputRef.current) fileInputRef.current.value = ""; // 重置 input
  };

  // --- 修改：处理发送消息 ---
  const handleSend = (e) => {
    e.preventDefault();
    // 如果既没有文本也没有文件，则不发送
    if (!newMessage.trim() && !selectedFile) return;

    // 模拟构建新消息对象
    const msg = {
      id: Date.now(),
      sender: CURRENT_USER_ROLE,
      text: newMessage,
      // 如果有预览图，暂时用预览图 URL 模拟发送成功的图片 URL
      // 在真实后端对接时，这里应该是上传成功后后端返回的 URL
      image: previewUrl || null, 
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages([...messages, msg]);
    
    // 发送后清空状态
    setNewMessage("");
    clearSelectedFile(); 
    // 注意：真实项目中，clearSelectedFile 不能在这里调用，
    // 因为 previewUrl 还要用于显示刚才发出去的消息。
    // 真实流程是：上传API -> 拿到真实URL -> 用真实URL创建消息 -> 清空本地预览
  };

  // 触发隐藏的文件输入框点击
  const triggerFileInput = () => {
    fileInputRef.current?.click();
  };


  return (
    <Card className="shadow-sm border-0 h-100">
      <Card.Header className="bg-white py-3 border-bottom">
        <div className="d-flex align-items-center justify-content-between gap-2">
          <strong>Live Chat</strong>
          <span className="badge bg-success rounded-pill " style={{fontSize: '0.7rem'}}>Online</span>
        </div>
      </Card.Header>

      {/* Message Section  */}
      <Card.Body 
        className="d-flex flex-column p-3 bg-light" 
        style={{ height: '400px', overflowY: 'auto' }}
      >
        {messages.map((msg) => {
          const isMe = msg.sender === CURRENT_USER_ROLE;
          const isSystem = msg.sender === 'system';

          if (isSystem) {
            return (
              <div key={msg.id} className="text-center my-2">
                <small className="text-muted bg-white px-2 py-1 rounded border">{msg.text}</small>
              </div>
            );
          }

          return (
            <div key={msg.id} className={`d-flex mb-3 ${isMe ? 'justify-content-end' : 'justify-content-start'}`}>
              <div 
                className={`p-2 rounded-3 shadow-sm ${
                  isMe ? 'bg-primary text-white' : 'bg-white text-dark border'
                }`}
                style={{ 
                  maxWidth: '75%',
                  borderBottomRightRadius: isMe ? '0' : '1rem',
                  borderBottomLeftRadius: isMe ? '1rem' : '0' 
                }}
              >
                {/* --- 修改：支持显示图片 --- */}
                {msg.image && (
                    <div className="mb-2">
                        <Image src={msg.image} alt="attachment" fluid rounded className="border" style={{maxHeight: '200px', objectFit: 'cover'}} />
                    </div>
                )}
                {/* 显示文本 (如果有) */}
                {msg.text && <div className="mb-1" style={{ fontSize: '0.9rem' }}>{msg.text}</div>}
                
                <div className={`text-end small ${isMe ? 'text-white-50' : 'text-muted'}`} style={{ fontSize: '0.7rem' }}>
                  {msg.time}
                </div>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </Card.Body>

      {/* 底部区域：包含预览和输入框 */}
      <Card.Footer className="bg-white py-3 border-top-0">
        
        {/* --- 新增：文件预览区域 (只有选中文件时才显示) --- */}
        {previewUrl && (
            <div className="mb-2 position-relative d-inline-block">
                <Image src={previewUrl} alt="Preview" thumbnail style={{ height: '80px', width: 'auto' }} />
                {/* 关闭按钮，用于取消选择 */}
                <CloseButton 
                    onClick={clearSelectedFile}
                    className="position-absolute top-0 start-100 translate-middle bg-white shadow-sm p-1" 
                    style={{fontSize: '0.7rem'}}
                />
            </div>
        )}

        <Form onSubmit={handleSend}>
          {/* --- 新增：隐藏的文件输入框 --- */}
          <input 
              type="file" 
              ref={fileInputRef} 
              onChange={handleFileSelect} 
              accept="image/png, image/jpeg, image/jpg" // 限制只能选图片，你可以去掉限制
              style={{ display: 'none' }} 
          />

          <InputGroup>
            {/* --- 新增：上传按钮 (回形针图标) --- */}
            <Button variant="outline-secondary" onClick={triggerFileInput} title="Attach File">
              <span style={{fontSize: '1.2rem'}}>File</span>
            </Button>
            
            <Form.Control
              placeholder="输入消息..."
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              className="border-secondary"
            />
            {/* 发送按钮：只要有文本 或者 有文件，就允许点击 */}
            <Button variant="primary" type="submit" disabled={!newMessage.trim() && !selectedFile}>
              Send
            </Button>
          </InputGroup>
        </Form>
      </Card.Footer>
    </Card>
  );
};

export default ChatRoom;