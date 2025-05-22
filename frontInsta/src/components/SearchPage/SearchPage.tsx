import React, {useEffect, useState} from 'react';
import './SearchPage.scss';
import {useNavigate} from 'react-router-dom';
import {useUserContext} from '../../context/UserContext';

interface User {
    id: string;
    username: string;
    email: string;
    image?: string;
    role?: string; // nou: presupunem că ai un câmp role
}

interface FriendRequestDTO {
    senderId: string;
    receiverId: string;
    status: string;
    id?: string;
}

interface FriendDTO {
    user1Id: string;
    user2Id: string;
    createdAt: string;
}

const SearchPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
    const [searchInput, setSearchInput] = useState('');
    const [friendRequests, setFriendRequests] = useState<FriendRequestDTO[]>([]);
    const [friends, setFriends] = useState<FriendDTO[]>([]);

    const navigate = useNavigate();
    const {user: currentUser} = useUserContext();
    const token = localStorage.getItem('token');

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/users/all`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await res.json();
                setUsers(
                    data.filter((user: User) => user.id !== currentUser?.id && user.role !== 'ADMIN')
                );


            } catch (error) {
                console.error('Error fetching users:', error);
            }
        };

        const fetchFriendRequests = async () => {
            try {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/friendRequests/all`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await res.json();
                setFriendRequests(data);
            } catch (error) {
                console.error('Error fetching friend requests:', error);
            }
        };

        const fetchFriends = async () => {
            try {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/friends/all`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await res.json();
                setFriends(data);
            } catch (error) {
                console.error('Error fetching friends:', error);
            }
        };

        fetchUsers();
        fetchFriendRequests();
        fetchFriends();
    }, [currentUser]);

    useEffect(() => {
        if (searchInput.trim() === '') {
            setFilteredUsers([]);
            return;
        }

        const loweredInput = searchInput.toLowerCase();
        const filtered = users.filter(
            (user) =>
                user.username.toLowerCase().includes(loweredInput) ||
                user.email.toLowerCase().includes(loweredInput)
        );
        setFilteredUsers(filtered);
    }, [searchInput, users]);

    const handleAddFriend = async (e: React.MouseEvent, receiverId: string) => {
        e.stopPropagation();

        if (!currentUser || currentUser.role === 'ADMIN') {
            alert('Admins cannot send friend requests.');
            return;
        }

        const payload: FriendRequestDTO = {
            senderId: currentUser.id,
            receiverId,
            status: 'PENDING',
        };

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/friendRequests/insert`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            const result = await res.json();
            alert(result.message);
            setFriendRequests((prevRequests) => [...prevRequests, payload]);
        } catch (err) {
            console.error('Failed to send friend request:', err);
            alert('Failed to send friend request.');
        }
    };

    const handleAcceptFriendRequest = async (
        e: React.MouseEvent,
        requestId: string,
        receiverId: string
    ) => {
        e.stopPropagation();

        if (!currentUser || currentUser.role === 'ADMIN') {
            alert('Admins cannot accept friend requests.');
            return;
        }

        try {
            await fetch(`${import.meta.env.VITE_API_URL}/friendRequests/delete/${requestId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const friendPayload: FriendDTO = {
                user1Id: currentUser.id,
                user2Id: receiverId,
                createdAt: new Date().toISOString(),
            };

            const friendRes = await fetch(`${import.meta.env.VITE_API_URL}/friends/insert`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(friendPayload),
            });

            const friendResult = await friendRes.json();
            alert(friendResult.message);
            setFriends((prev) => [...prev, friendPayload]);
        } catch (err) {
            console.error('Failed to accept friend request:', err);
            alert('Failed to accept friend request.');
        }
    };

    const areFriends = (userId: string) => {
        return friends.some(
            (friend) =>
                (friend.user1Id === currentUser?.id && friend.user2Id === userId) ||
                (friend.user2Id === currentUser?.id && friend.user1Id === userId)
        );
    };

    const hasRequested = (userId: string) => {
        return friendRequests.some(
            (request) =>
                request.senderId === currentUser?.id && request.receiverId === userId
        );
    };

    const hasReceivedRequest = (userId: string) => {
        return friendRequests.some(
            (request) =>
                request.receiverId === currentUser?.id && request.senderId === userId
        );
    };

    return (
        <div className="search-page">
            <h2>Search Users</h2>
            <input
                type="text"
                placeholder="Type to search users..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
            />

            {searchInput.trim() !== '' && (
                <ul>
                    {filteredUsers.length === 0 ? (
                        <li>No users found.</li>
                    ) : (
                        filteredUsers.map((user) => (
                            <li key={user.id} onClick={() => navigate(`/user/${user.id}`)}>
                                <div className="content">
                                    <img
                                        src={
                                            user.image
                                                ? `data:image/jpeg;base64,${user.image}`
                                                : 'https://www.gravatar.com/avatar/?d=identicon'
                                        }
                                        alt={user.username}
                                        className="user-avatar"
                                    />
                                    <span className="user-content">{user.username}</span>
                                </div>

                                {/* Ascunde toate butoanele dacă oricare din utilizatori e admin */}
                                {user.role !== 'ADMIN' && currentUser?.role !== 'ADMIN' ? (
                                    areFriends(user.id) ? (
                                        <div className="button-group">
                                            <button onClick={() => navigate(`/user/${user.id}`)}>View</button>
                                        </div>
                                    ) : hasReceivedRequest(user.id) ? (
                                        <button
                                            onClick={(e) => {
                                                const requestId =
                                                    friendRequests.find((req) => req.senderId === user.id)?.id || '';
                                                handleAcceptFriendRequest(e, requestId, user.id);
                                            }}
                                        >
                                            Accept
                                        </button>
                                    ) : hasRequested(user.id) ? (
                                        <button>Requested</button>
                                    ) : (
                                        <button onClick={(e) => handleAddFriend(e, user.id)}>Add</button>
                                    )
                                ) : (
                                    // Dacă userul este admin sau currentUser este admin, nu afișa butoane
                                    null
                                )}

                            </li>
                        ))
                    )}
                </ul>
            )}
        </div>
    );
};

export default SearchPage;
