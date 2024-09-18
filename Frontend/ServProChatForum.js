import React, { useState, useEffect } from 'react';
import {
  TextField,
  IconButton,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Typography,
} from '@mui/material';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {useUserId} from './UserIdContext';
import { set } from 'rsuite/esm/internals/utils/date';
import './servprochatforum.css';

export default function ServiceProviderChatForum  ({serviceType,username}) {
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [nickname, setNickname] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const {id} = useUserId();
  const [replyToMessageId, setReplyToMessageId] = useState(null);
  const [roomId, setRoomId] = useState(null);

  const serviceTypeToRoomIdMap = {
    'Transport': 1,
    'Event': 2,
    'Hotel': 3,
    'Course': 4,
    'TravelLocation': 5,
    'Package': 6,
  };

  useEffect(() => {
    if (serviceType) {
      const roomId = serviceTypeToRoomIdMap[serviceType];
      setRoomId(roomId);
      console.log('Room ID:', roomId);
    }
  }, [serviceType]);

  useEffect(() => {
   if(id&& !isConnected &&roomId){
    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);

 // if(!client){
      //  if(!isConnected){

    client.connect({}, () => {

       

      client.subscribe(`/topic/messages/${roomId}`, (message) => {
        const receivedMessage = JSON.parse(message.body);
        console.log("thisss Received message:",receivedMessage);
        console.log("thisss all messages:",messages);
        setMessages((prevMessages) => [...prevMessages, receivedMessage]);
        console.log("After addition thisss all messages:",messages);

      });
      setIsConnected(true); // Connection established
    console.log('Subscribing to /topic/messages/',roomId);
    });
    
   // }
    

    setStompClient(client);
 // }
    }
    return () => {
    //   client.disconnect();
    console.log('Disconnecting WebSocket');

    if (stompClient) {
        stompClient.disconnect(() => setIsConnected(false)); // Clean up on component unmount
   // client.disconnect();
      }

    // if(client){
    //     client.disconnect();
    //     setIsConnected(false);
    // }


    };
  }, [id,roomId,]);

  const handleNicknameChange = (event) => {
    setNickname(event.target.value);
  };

  const handleMessageChange = (event) => {
    setMessage(event.target.value);
  };


  // useEffect(() => {
  //   console.log("Updated messages:", messages);
  // }, [messages]);
  // useEffect(() => {
  //   console.log("thisss Updated message:", message);
  // }, [message]);

  useEffect(() => {
    console.log('Component mounted');
    return () => {
      console.log('Component unmounted');
    };
  }, [])

  const getServiceProviderChatRoomMessages = async()=>{
    try{
      const response = await fetch('http://localhost:8080/chatMessage/getServiceProviderMessages', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        //body: JSON.stringify({ id: "1" }),
        body: JSON.stringify({id:roomId}),
      });
      const data = await response.json();
      // console.log("all message data:",data);
      console.log("all message response:",data);
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      //if(data!=null){
        setMessages(data);  

      //}
    }
    catch(error){
      console.log("Error fetching chat room messages",error );
    }
  }

  useEffect(()=>{
    if(roomId){
      getServiceProviderChatRoomMessages();
    }
  },[roomId])

  const sendMessage = () => {
    if (message.trim()) {
      const chatMessage = {
        // nickname,
        // content: message,
        messageText:message,
        messageSenderName:username,
        reply: !!replyToMessageId,
        replyMessageId: replyToMessageId || null,
        chatForumIdCode:roomId,
        senderId:id,

      };
      console.log("Chat message to send",chatMessage);

      stompClient.send(`/app/chat/${roomId}`, {}, JSON.stringify(chatMessage));
      //saveServiceProviderChatRoomMessage();
      setMessage('');
     setReplyToMessageId(null); // Reset reply state after sending

    }
  };

//   const saveServiceProviderChatRoomMessage = async()=>{
//     if (message.trim()) {
//       const chatMessage = {
//         // nickname,
//         // content: message,
//         messageText:message,
//         messageSenderName:nickname,
//         chatForumId:1,
//         senderId:id,
//         reply: !!replyToMessageId,
//         replyMessageId: replyToMessageId || 0,
//       };
//       console.log("Chat message to save",chatMessage);
//     try{
//      const response = await fetch('http://localhost:8080/chatMessage/addServiceProviderMessage',{
//       method:'POST',
//       headers:{'Content-Type':'application/json'},
//       body: JSON.stringify(chatMessage)
//      });

//      if (!response.ok) {
//       throw new Error(`HTTP error! Status: ${response.status}`);
//     }
//     // const data = await response.json();
// console.log("Message saved",response);  
// // console.log("Message saved",data);
//      setMessage('');
//      setReplyToMessageId(null); // Reset reply state after sending

//     }catch(error){
//       console.log("Error saving chat room message",error);
//     }
//   }
//   }
  
  const handleReply = (messageId) => {
    setReplyToMessageId(messageId);
  };

  return (
    <div>
              <h2 style={{ margin:'15px auto' }}>Chat Forum</h2>

  <div className="chat-container">
  <ul className="chat-room">
    {messages.map((msg, index) => (
      <li key={index} className="chat-message">
        {msg.reply && (
          <div className="reply-info">
            {messages.find(
              originalMessage => originalMessage.messageId === msg.replyMessageId
            ) && (
              <>
                <div className="reply-sender">
                  Replying to:{" "}
                  {messages.find(
                    originalMessage => originalMessage.messageId === msg.replyMessageId
                  )?.messageSenderName}
                </div>
                <div className="reply-text">
                  {messages.find(
                    originalMessage => originalMessage.messageId === msg.replyMessageId
                  )?.messageText}
                </div>
              </>
            )}
          </div>
        )}
        <div className="message-sender">{msg.messageSenderName}</div>
        <div className="message-text">{msg.messageText}</div>
        <button className="reply-button" onClick={() => handleReply(msg.messageId)}>
          Reply
        </button>
      </li>
    ))}
  </ul>
  <div style={{ display: 'flex', alignItems: 'center' }}>
        <TextField
          placeholder="Enter your nickname"
          value={username}
          autoFocus
          InputProps={{
            readOnly: true,
          }}
        />
        <TextField
          placeholder="Type a message"
          value={message}
          onChange={handleMessageChange}
          fullWidth
        />
        <IconButton onClick={sendMessage} disabled={!message.trim()}>
          Send
        </IconButton>
      </div>
</div>
   
    </div>
  );
};

