import React, {useEffect, useState} from 'react';
import './SearchChat.scss';
import {useNavigate} from 'react-router-dom';
import {useUserContext} from '../../context/UserContext';

interface User {
    id: string;
    username: string;
    email: string;
    image?: string;
}

interface FriendDTO {
    user1Id: string;
    user2Id: string;
    createdAt: string;
}

const SearchPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [friendUsers, setFriendUsers] = useState<User[]>([]);
    const [searchInput, setSearchInput] = useState('');
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
                setUsers(data.filter((user: User) => user.id !== currentUser?.id));
            } catch (error) {
                console.error('Error fetching users:', error);
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
        fetchFriends();
    }, [currentUser]);

    useEffect(() => {
        const matchedFriends = users.filter((user) =>
            friends.some(
                (friend) =>
                    (friend.user1Id === currentUser?.id && friend.user2Id === user.id) ||
                    (friend.user2Id === currentUser?.id && friend.user1Id === user.id)
            )
        );
        setFriendUsers(matchedFriends);
    }, [users, friends, currentUser]);

    const displayedFriends = friendUsers.filter((user) =>
        user.username.toLowerCase().includes(searchInput.toLowerCase()) ||
        user.email.toLowerCase().includes(searchInput.toLowerCase())
    );

    return (
        <div className="search-page">
            <h2>Conversations</h2>
            <input
                type="text"
                placeholder="Search conversation..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
            />
            <ul>
                {displayedFriends.length === 0 ? (
                    <li>No friends found.</li>
                ) : (
                    displayedFriends.map((user) => (
                        <li key={user.id} onClick={() => navigate(`/chat/${user.id}`)}>
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
                        </li>
                    ))
                )}
            </ul>
        </div>
    );
};

export default SearchPage;
