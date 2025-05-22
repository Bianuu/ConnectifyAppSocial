import React, {useEffect, useState} from 'react';
import './FriendsPage.scss';
import {useLocation, useNavigate} from 'react-router-dom';
import {useUserContext} from '../../context/UserContext';

interface User {
    id: string;
    username: string;
    email: string;
    image?: string;
}

interface FriendRequestDTO {
    id: string;
    senderId: string;
    receiverId: string;
    status: string;
}

interface FriendDTO {
    id: string;
    user1Id: string;
    user2Id: string;
    createdAt: string;
}

const FriendsPage: React.FC = () => {
    const location = useLocation();
    const {user: currentUser} = useUserContext();
    const [activeTab, setActiveTab] = useState(location.state?.tab || 'friends');

    const [users, setUsers] = useState<User[]>([]);
    const [friendRequests, setFriendRequests] = useState<FriendRequestDTO[]>([]);
    const [friends, setFriends] = useState<FriendDTO[]>([]);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchAll = async () => {
            try {
                const token = localStorage.getItem('token');
                if (!token) throw new Error('No auth token');

                const [usersRes, requestsRes, friendsRes] = await Promise.all([
                    fetch(`${import.meta.env.VITE_API_URL}/users/all`, {
                        headers: {Authorization: `Bearer ${token}`}
                    }),
                    fetch(`${import.meta.env.VITE_API_URL}/friendRequests/all`, {
                        headers: {Authorization: `Bearer ${token}`}
                    }),
                    fetch(`${import.meta.env.VITE_API_URL}/friends/all`, {
                        headers: {Authorization: `Bearer ${token}`}
                    }),
                ]);

                if (!usersRes.ok || !requestsRes.ok || !friendsRes.ok) {
                    throw new Error('Failed to fetch one or more resources');
                }

                const usersData = await usersRes.json();
                const requestsData = await requestsRes.json();
                const friendsData = await friendsRes.json();

                setUsers(usersData);
                setFriendRequests(requestsData);
                setFriends(friendsData);
            } catch (error) {
                console.error('Failed to fetch data:', error);
            }
        };

        fetchAll();
    }, [activeTab]);


    const getUserById = (id: string) => users.find((u) => u.id === id);

    const requested = friendRequests.filter((r) => r.senderId === currentUser?.id);

    const handleDeleteFriend = async (id: string) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) throw new Error('No auth token');

            await fetch(`${import.meta.env.VITE_API_URL}/friends/delete/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            setFriends((prev) => prev.filter((f) => f.id !== id));
        } catch (err) {
            console.error('Failed to delete friend:', err);
        }
    };


    const renderFriends = () => {
        const currentFriends = friends.filter(
            (f) => f.user1Id === currentUser?.id || f.user2Id === currentUser?.id
        );

        if (currentFriends.length === 0) return <p className="empty">No friends yet.</p>;

        return currentFriends.map((friend) => {
            const friendId = friend.user1Id === currentUser?.id ? friend.user2Id : friend.user1Id;
            const user = getUserById(friendId);
            if (!user) return null;

            return (
                <li key={friend.id}>
                    <div className="requested-content" onClick={() => navigate(`/user/${friendId}`)}>
                        <img
                            src={
                                user.image
                                    ? `data:image/jpeg;base64,${user.image}`
                                    : 'https://www.gravatar.com/avatar/?d=identicon'
                            }
                            alt={user.username}
                            className="avatar"
                        />
                        <span>{user.username}</span>
                    </div>
                    <div className="actions">
                        <button onClick={() => handleDeleteFriend(friend.id)}>Delete</button>
                    </div>
                </li>
            );
        });
    };

    const handleAcceptRequest = async (request: FriendRequestDTO) => {
        const friendDTO = {
            user1Id: request.senderId,
            user2Id: request.receiverId,
            createdAt: new Date(),
        };

        try {
            const token = localStorage.getItem('token');
            await fetch(`${import.meta.env.VITE_API_URL}/friends/insert`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(friendDTO),
            });


            await handleDeleteRequest(request.id);
        } catch (err) {
            console.error('Failed to accept friend request:', err);
        }
    };

    const renderIncoming = () => {
        const incoming = friendRequests.filter((r) => r.receiverId === currentUser?.id);

        if (incoming.length === 0) return <p className="empty">No incoming requests.</p>;

        return incoming.map((request) => {
            const user = getUserById(request.senderId);
            if (!user) return null;

            return (
                <li key={request.id}>
                    <div className="requested-content" onClick={() => navigate(`/user/${request.senderId}`)}>
                        <img
                            src={
                                user.image
                                    ? `data:image/jpeg;base64,${user.image}`
                                    : 'https://www.gravatar.com/avatar/?d=identicon'
                            }
                            alt={user.username}
                            className="avatar"
                        />
                        <span>{user.username}</span>
                    </div>
                    <div className="actions">
                        <button onClick={() => handleAcceptRequest(request)}>Accept</button>
                        <button onClick={() => handleDeleteRequest(request.id)}>Delete</button>
                    </div>
                </li>
            );
        });
    };

    const renderRequested = () => {
        if (requested.length === 0) return <p className="empty">No sent requests.</p>;

        return requested.map((request) => {
            const user = getUserById(request.receiverId);
            if (!user) return null;

            return (
                <li key={request.id}>
                    <div className="requested-content" onClick={() => navigate(`/user/${request.receiverId}`)}>
                        <img
                            src={
                                user.image
                                    ? `data:image/jpeg;base64,${user.image}`
                                    : 'https://www.gravatar.com/avatar/?d=identicon'
                            }
                            alt={user.username}
                            className="avatar"
                        />
                        <span>{user.username}</span>
                    </div>
                    <div className="actions">
                        <button onClick={() => handleDeleteRequest(request.id)}>Delete</button>
                    </div>
                </li>
            );
        });
    };

    const handleDeleteRequest = async (id: string) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) throw new Error('No auth token');

            await fetch(`${import.meta.env.VITE_API_URL}/friendRequests/delete/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            setFriendRequests((prev) => prev.filter((r) => r.id !== id));
        } catch (err) {
            console.error('Failed to delete request:', err);
        }
    };

    return (
        <div className="friends-page">
            <div className="tabs">
                <button className={activeTab === 'friends' ? 'active' : ''} onClick={() => setActiveTab('friends')}>
                    Friends
                </button>
                <button className={activeTab === 'requested' ? 'active' : ''} onClick={() => setActiveTab('requested')}>
                    Requested
                </button>
                <button className={activeTab === 'requests' ? 'active' : ''} onClick={() => setActiveTab('requests')}>
                    Incoming
                </button>
            </div>

            <ul className="user-list">
                {activeTab === 'requested' && renderRequested()}
                {activeTab === 'friends' && renderFriends()}
                {activeTab === 'requests' && renderIncoming()}
            </ul>
        </div>
    );
};

export default FriendsPage;
