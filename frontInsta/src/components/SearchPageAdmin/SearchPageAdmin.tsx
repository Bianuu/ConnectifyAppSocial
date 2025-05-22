import React, {useEffect, useState} from 'react';
import './SearchPageAdmin.scss';
import {useNavigate} from 'react-router-dom';
import {useUserContext} from '../../context/UserContext';

interface User {
    id: string;
    username: string;
    email: string;
    image?: string;
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
                    data.filter(
                        (user: User) =>
                            user.id !== currentUser?.id && user.email !== 'admin@yahoo.com'
                    )
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
                                {/* Butoane eliminate */}
                            </li>
                        ))
                    )}
                </ul>
            )}

        </div>
    );
};

export default SearchPage;
