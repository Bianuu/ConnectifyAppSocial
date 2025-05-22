import React, {useEffect, useRef, useState} from 'react';
import {useParams} from 'react-router-dom';
import {useUserContext} from '../../context/UserContext';
import './ChatPage.scss';

interface MessageDTO {
    id?: string;
    senderMessage: string;
    receiverMessage: string;
    content: string;
    sentAt?: string;
}

interface UserDTO {
    id: string;
    username: string;
}

const ChatPage: React.FC = () => {
    const {userId: receiverId} = useParams();
    const {user: currentUser} = useUserContext();
    const [receiverName, setReceiverName] = useState('');
    const [messages, setMessages] = useState<MessageDTO[]>([]);
    const [newMessage, setNewMessage] = useState('');
    const token = localStorage.getItem('token');
    const messagesEndRef = useRef<HTMLDivElement | null>(null);
    const chatContainerRef = useRef<HTMLDivElement | null>(null);

    const fetchMessages = async () => {
        if (!receiverId || !currentUser) return;
        try {
            const urls = [
                `${import.meta.env.VITE_API_URL}/messages/messagesUsers/${currentUser.id}/${receiverId}`,
                `${import.meta.env.VITE_API_URL}/messages/messagesUsers/${receiverId}/${currentUser.id}`,
            ];
            const responses = await Promise.all(
                urls.map((url) =>
                    fetch(url, {headers: {Authorization: `Bearer ${token}`}})
                )
            );
            const data = await Promise.all(
                responses.map((res) => (res.ok ? res.json() : Promise.resolve([])))
            );
            const combined = [...data[0], ...data[1]].sort(
                (a, b) => new Date(a.sentAt || '').getTime() - new Date(b.sentAt || '').getTime()
            );
            setMessages(combined);
        } catch (error) {
            console.error('Error fetching messages:', error);
            setMessages([]);
        }
    };

    useEffect(() => {
        const fetchReceiverName = async () => {
            if (!receiverId) return;
            try {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/users/${receiverId}`, {
                    headers: {Authorization: `Bearer ${token}`},
                });
                if (res.ok) {
                    const data: UserDTO = await res.json();
                    setReceiverName(data.username);
                } else {
                    setReceiverName('Unknown User');
                }
            } catch {
                setReceiverName('Unknown User');
            }
        };
        fetchReceiverName();
    }, [receiverId]);

    useEffect(() => {
        fetchMessages();
    }, [receiverId, currentUser]);

    useEffect(() => {
        const interval = setInterval(() => {
            fetchMessages();
        }, 3000);
        return () => clearInterval(interval);
    }, [receiverId, currentUser]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({behavior: 'smooth'});
    }, [messages]);


    const handleSend = async () => {
        if (!newMessage.trim() || !receiverId || !currentUser) return;
        const payload: MessageDTO = {
            senderMessage: currentUser.id,
            receiverMessage: receiverId,
            content: newMessage.trim(),
        };
        const res = await fetch(`${import.meta.env.VITE_API_URL}/messages/insert`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        });
        if (res.ok) {
            const sentAt = new Date().toISOString();
            setNewMessage('');
            setMessages((prev) => [...prev, {...payload, sentAt}]);
        }
    };

    return (
        <div className="chat-page futuristic-glow">
            <div className="chat-header shimmer-text">{receiverName || 'Chat'}</div>
            <div className="chat-messages fancy-scroll" ref={chatContainerRef}>
                {messages.map((msg, index) => (
                    <div
                        key={index}
                        className={`message neon-bubble ${msg.senderMessage === currentUser?.id ? 'my-message' : 'their-message'}`}
                    >
                        <div>{msg.content}</div>
                        {msg.sentAt && (
                            <div className="timestamp">
                                {new Date(msg.sentAt).toLocaleTimeString([], {
                                    hour: '2-digit',
                                    minute: '2-digit',
                                })}
                            </div>
                        )}
                    </div>
                ))}
                <div ref={messagesEndRef}/>
            </div>
            <div className="chat-input glass-effect">
                <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter' && !e.shiftKey) {
                            e.preventDefault();
                            handleSend();
                        }
                    }}
                    placeholder="Type your message..."
                />
                <button onClick={handleSend} className="glow-button">Send</button>
            </div>
        </div>
    );
};

export default ChatPage;
