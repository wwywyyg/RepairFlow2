import { Client } from "@stomp/stompjs";

let client = null;

export const connectWs = ({ token, onConnect, onError }) => {
  client = new Client({
    brokerURL: "ws://localhost:8080/ws",
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    reconnectDelay: 3000,
    onConnect: () => onConnect?.(),
    onStompError: (frame) => onError?.(frame),
    onWebSocketError: (evt) => onError?.(evt),
  });

  client.activate();
  return client;
};

export const disconnectWs = () => {
  if (client) client.deactivate();
  client = null;
};

export const subscribeTicket = (ticketId, onMessage) => {
  if (!client || !client.connected) throw new Error("WS not connected yet");
  return client.subscribe(`/topic/ticket/${ticketId}`, (msg) => {
    onMessage(JSON.parse(msg.body));
  });
};

export const sendTicketMessage = (ticketId, payload) => {
  if (!client || !client.connected) throw new Error("WS not connected yet");
  client.publish({
    destination: `/app/ticket/${ticketId}/message`,
    body: JSON.stringify(payload),
  });
};
