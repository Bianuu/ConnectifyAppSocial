import React, {useEffect, useState} from 'react';
import {useUserContext} from '../../context/UserContext';
import {Link} from 'react-router-dom';
import './SuggestedFriends.scss'

interface FriendDTO {
    id: string;
    user1Id: string;
    user2Id: string;
    createdAt: string;
}

interface User {
    id: string;
    username: string;
    email: string;
    image?: string;
}

const SuggestedFriends: React.FC = () => {
    const {user: currentUser} = useUserContext();
    const [users, setUsers] = useState<User[]>([]);
    const [friends, setFriends] = useState<FriendDTO[]>([]);
    const [suggested, setSuggested] = useState<User[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem('token');
            const headers = {Authorization: `Bearer ${token}`};

            const [usersRes, friendsRes] = await Promise.all([
                fetch(`${import.meta.env.VITE_API_URL}/users/all`, {headers}),
                fetch(`${import.meta.env.VITE_API_URL}/friends/all`, {headers}),
            ]);

            const usersData = await usersRes.json();
            const friendsData = await friendsRes.json();

            setUsers(usersData);
            setFriends(friendsData);
        };

        if (currentUser) fetchData();
    }, [currentUser]);

    useEffect(() => {
        if (!currentUser || users.length === 0 || friends.length === 0) return;

        const directFriends = new Set<string>();
        friends.forEach(f => {
            if (f.user1Id === currentUser.id) directFriends.add(f.user2Id);
            else if (f.user2Id === currentUser.id) directFriends.add(f.user1Id);
        });

        const friendOfFriends = new Map<string, number>();

        directFriends.forEach(friendId => {
            friends.forEach(f => {
                const other =
                    f.user1Id === friendId ? f.user2Id :
                        f.user2Id === friendId ? f.user1Id : null;

                if (other && other !== currentUser.id && !directFriends.has(other)) {
                    friendOfFriends.set(other, (friendOfFriends.get(other) || 0) + 1);
                }
            });
        });

        const sortedSuggestions = Array.from(friendOfFriends.entries())
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5)
            .map(([userId]) => users.find(u => u.id === userId)!)
            .filter(Boolean);

        setSuggested(sortedSuggestions);
    }, [currentUser, users, friends]);

    return (
        <div className="suggested-friends">
            <h2>People You May Know</h2>
            {suggested.length === 0 ? (
                <p>No suggestions available.</p>
            ) : (
                <div className="suggestions-carousel">
                    {suggested.map(user => (
                        <Link to={`/user/${user.id}`} className="suggested-item" key={user.id}>
                            <img
                                src={user.image ? `data:image/jpeg;base64,${user.image}` : 'https://www.gravatar.com/avatar/?d=identicon'}
                                alt={user.username}
                                className="avatar"
                            />
                            <span>{user.username}</span>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );

};

export default SuggestedFriends;
