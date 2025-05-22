import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import './UserProfilePage.scss';
import {useUserContext} from '../../context/UserContext';
import UserPosts from '../Profile/UserPosts';
import AlbumGallery from '../Profile/AlbumGallery';

interface User {
    id: string;
    username: string;
    email: string;
    bio: string;
    dateOfBirth: string;
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

const UserProfilePage: React.FC = () => {
    const {userId} = useParams<{ userId: string }>();
    const [user, setUser] = useState<User | null>(null);
    const [friendRequests, setFriendRequests] = useState<FriendRequestDTO[]>([]);
    const [friends, setFriends] = useState<FriendDTO[]>([]);
    const [requestSent, setRequestSent] = useState(false);
    const {user: currentUser} = useUserContext();
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState<'Postări' | 'Albume'>('Postări');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const token = localStorage.getItem('token') || '';

                const [usersRes, requestsRes, friendsRes] = await Promise.all([
                    fetch(`${import.meta.env.VITE_API_URL}/users/all`, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    }),
                    fetch(`${import.meta.env.VITE_API_URL}/friendRequests/all`, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    }),
                    fetch(`${import.meta.env.VITE_API_URL}/friends/all`, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    }),
                ]);

                const users: User[] = await usersRes.json();
                const requests: FriendRequestDTO[] = await requestsRes.json();
                const friendsList: FriendDTO[] = await friendsRes.json();

                const foundUser = users.find(u => u.id === userId);
                if (foundUser) setUser(foundUser);

                setFriendRequests(requests);
                setFriends(friendsList);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        if (userId) fetchData();
    }, [userId]);

    if (!user) return <div className="profile-page">Loading user profile...</div>;

    const isRequested = friendRequests.some(
        r => r.senderId === currentUser?.id && r.receiverId === user?.id
    );
    const isIncoming = friendRequests.some(
        r => r.receiverId === currentUser?.id && r.senderId === user?.id
    );
    const isFriend = friends.some(
        f =>
            (f.user1Id === currentUser?.id && f.user2Id === user?.id) ||
            (f.user2Id === currentUser?.id && f.user1Id === user?.id)
    );

    const handleAddFriend = async () => {
        if (!currentUser) {
            alert('Please log in to add friends.');
            return;
        }

        const payload = {
            senderId: currentUser.id,
            receiverId: user?.id,
            status: 'PENDING',
        };

        try {
            const token = localStorage.getItem('token');
            const res = await fetch(
                `${import.meta.env.VITE_API_URL}/friendRequests/insert`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify(payload),
                }
            );

            if (res.ok) {
                alert('Friend request sent!');
                setRequestSent(true);
            } else if (res.status === 500) {
                alert('A apărut o eroare pe server. Te rugăm încearcă mai târziu.');
            } else {
                const result = await res.json();
                alert(result.message || 'Failed to send friend request.');
            }
        } catch (err) {
            console.error('Error sending friend request:', err);
            alert('Conexiune eșuată. Verifică rețeaua și încearcă din nou.');
        }
    };

    const handleMessageClick = () => {
        navigate(`/chat/${user?.id}`);
    };

    const isAdmin = currentUser?.role === 'ADMIN' || currentUser?.email === 'admin@yahoo.com';
    const canShowAddButton = !isRequested && !isIncoming && !isFriend && !requestSent && !isAdmin;

    const formatDate = (dateString: string | undefined): string => {
        if (!dateString) return 'Not provided';
        return new Date(dateString).toLocaleDateString();
    };

    return (
        <div className="profile-page">
            <div className="profile-header">
                <div className="profile-pic-wrapper">
                    <img
                        src={
                            user?.image
                                ? `data:image/jpeg;base64,${user.image}`
                                : 'https://www.gravatar.com/avatar/?d=identicon'
                        }
                        alt={user?.username || 'User'}
                        className="profile-image"
                    />
                </div>
                <div className="profile-info-wrapper">
                    <div className="profile-top-row">
                        <h2 className="profile-username">{user?.username || 'User'}</h2>
                        {currentUser?.id !== user?.id && isFriend && (
                            <button className="message-btn" onClick={handleMessageClick}>
                                Message
                            </button>
                        )}
                        {user && canShowAddButton && (
                            <button
                                className="add-friend-button"
                                onClick={handleAddFriend}
                                disabled={requestSent}
                            >
                                {requestSent ? 'Requested' : 'Add Friend'}
                            </button>
                        )}
                    </div>
                    <div className="profile-meta">
                        <p className="profile-meta-item">
                            <strong>Date of Birth:</strong> {formatDate(user?.dateOfBirth)}
                        </p>
                        <p className="profile-meta-item">
                            <strong>Email:</strong> {user?.email || 'Not provided'}
                        </p>
                        <p className="profile-meta-item">
                            <strong>Bio:</strong> {user?.bio || 'Not provided'}
                        </p>
                    </div>
                    <div className="profile-actions">
                        {(isRequested || requestSent) && (
                            <span className="status status--requested">Request Sent</span>
                        )}
                        {isIncoming && (
                            <span className="status status--incoming">Received Request</span>
                        )}
                        {isFriend && (
                            <span className="status status--friends">Friends</span>
                        )}
                    </div>
                </div>
            </div>

            {user && (
                <div className="tab-panel">
                    <div className="tab-bar">
                        <button
                            className={`tab-button ${activeTab === 'Postări' ? 'active' : ''}`}
                            onClick={() => setActiveTab('Postări')}
                        >
                            Postări
                        </button>
                        <button
                            className={`tab-button ${activeTab === 'Albume' ? 'active' : ''}`}
                            onClick={() => setActiveTab('Albume')}
                        >
                            Albume
                        </button>
                    </div>
                    <div className="tab-content">
                        {activeTab === 'Postări' && (
                            <UserPosts userId={user.id} token={localStorage.getItem('token') || ''}/>
                        )}
                        {activeTab === 'Albume' && (
                            <AlbumGallery userId={user.id} token={localStorage.getItem('token') || ''}/>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserProfilePage;
