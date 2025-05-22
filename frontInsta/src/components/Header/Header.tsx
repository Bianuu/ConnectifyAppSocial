import React, {useEffect, useState} from 'react';
import './Header.scss';
import {useUserContext} from '../../context/UserContext';
import {useNavigate} from 'react-router-dom';

const Header: React.FC = () => {
    const {user, setUser} = useUserContext();
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);

    useEffect(() => {
        const fetchUserDetails = async () => {
            if (!user || !user.id) return;

            try {
                const token = localStorage.getItem('token');
                const res = await fetch(`${import.meta.env.VITE_API_URL}/users/${user.id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (!res.ok) {
                    throw new Error('Failed to fetch user data');
                }

                const userData = await res.json();
                setUser(userData);
            } catch (error) {
                console.error('Error fetching user data:', error);
            }
        };

        fetchUserDetails();
    }, [user?.id, setUser]);

    const handleLogout = () => {
        // Clear all authentication data
        setUser(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token'); // Also remove the token

        // Navigate to landing page
        navigate('/');
    };

    const handleProfileClick = () => {
        navigate('/profile');
    };

    return (
        <header className="header">
            <div className="logo">
                {/* Textul de logo cu caractere speciale și clasă pentru animație */}
                <h1 className="logo-text">𝓒𝓸𝓷𝓷𝓮𝓬𝓽𝓲𝓯𝔂</h1>
            </div>
            <div
                className="user-info"
                onMouseEnter={() => setShowDropdown(true)}
                onMouseLeave={() => setShowDropdown(false)}
            >
                {user ? (
                    <>
                        <img
                            src={
                                user.image
                                    ? `data:image/jpeg;base64,${user.image}`
                                    : 'https://www.gravatar.com/avatar/?d=identicon'
                            }
                            alt={user.username}
                            className="user-image"/>
                        <span>{user.username}</span>
                        {showDropdown && (
                            <div className="dropdown">
                                {user.email !== 'admin@yahoo.com' && (
                                    <button onClick={handleProfileClick}>View Profile</button>
                                )}
                                <button onClick={handleLogout}>Log Out</button>
                            </div>
                        )}

                    </>
                ) : (
                    <span> </span>
                )}
            </div>
        </header>
    );
};

export default Header;