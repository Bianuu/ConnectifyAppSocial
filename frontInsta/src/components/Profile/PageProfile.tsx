import React, {useEffect, useState} from 'react';
import {useUserContext} from '../../context/UserContext';
import './PageProfile.scss';
import UserPosts from './UserPosts';
import AlbumGallery from './AlbumGallery';

const ProfilePage: React.FC = () => {
    const {user, setUser} = useUserContext();
    const [error, setError] = useState<string>("");
    const [formData, setFormData] = useState({
        id: '',
        email: '',
        username: '',
        bio: '',
        dateOfBirth: '',
        password: '',
        image: null as string | null,
        createdAt: '',
        updatedAt: '',
    });
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [isEditing, setIsEditing] = useState(false);
    const [imageChanged, setImageChanged] = useState(false);

    const [activeTab, setActiveTab] = useState<'Postări' | 'Albume'>('Postări');

    useEffect(() => {
        const fetchUserById = async () => {
            try {
                if (!user?.id) return;

                const token = localStorage.getItem('token');
                if (!token) {
                    console.warn('No auth token found');
                    return;
                }

                const response = await fetch(`${import.meta.env.VITE_API_URL}/users/${user.id}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    console.error('Failed to fetch user data');
                    return;
                }

                const userData = await response.json();
                const formattedDateOfBirth = userData.dateOfBirth
                    ? new Date(userData.dateOfBirth).toISOString().split('T')[0]
                    : '';
                const today = new Date().toISOString().split('T')[0];

                setFormData({
                    id: userData.id,
                    email: userData.email || '',
                    username: userData.username || '',
                    bio: userData.bio || '',
                    dateOfBirth: formattedDateOfBirth,
                    password: '',
                    image: userData.image || null,
                    createdAt: userData.createdAt || today,
                    updatedAt: today,
                });

                setUser((prevUser) => ({
                    ...prevUser,
                    ...userData
                }));

            } catch (error) {
                console.error('Error fetching user by ID:', error);
            }
        };

        fetchUserById();
    }, [user?.id, setUser]);

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        const {name, value} = e.target;
        setFormData((prev) => ({...prev, [name]: value}));
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            setSelectedFile(file);
            setImageChanged(true);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (formData.password.trim().toLowerCase() === 'reset') {
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    setError('Nu ești autentificat.');
                    return;
                }

                const res = await fetch(
                    `${import.meta.env.VITE_API_URL}/users/reset-password/${user?.id}`,
                    {
                        method: 'POST',
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                if (res.ok) {
                    alert('Emailul pentru resetarea parolei a fost trimis.');
                    setError('');
                    setIsEditing(false);
                } else {
                    const errorText = await res.text();
                    console.error('Reset failed:', errorText);
                    setError('A apărut o eroare la trimiterea emailului de resetare.');
                }
            } catch (err) {
                console.error('Eroare la resetarea parolei:', err);
                setError('Eroare la rețea sau server.');
            }
            return;
        }

        if (!formData.password.trim()) {
            setError('Vă rugăm să introduceți parola pentru a vă actualiza profilul sau scrieți "reset" pentru resetare.');
            return;
        }

        const payload = {
            id: formData.id,
            email: formData.email,
            username: formData.username,
            bio: formData.bio,
            dateOfBirth: formData.dateOfBirth,
            password: formData.password,
            createdAt: formData.createdAt,
            updatedAt: formData.updatedAt,
            image: formData.image,
        };

        const token = localStorage.getItem('token');

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/users/update`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify(payload),
                }
            );
            if (!response.ok) {
                const errorResponse = await response.json();
                console.error('Error response:', errorResponse);
                return;
            }
            const result = await response.json();
            console.log('User updated:', result);

            const updatedUser = {
                id: formData.id,
                role: user?.role,
                email: formData.email,
                username: formData.username,
                password: formData.password,
                bio: formData.bio,
                createdAt: formData.createdAt,
                updatedAt: formData.updatedAt,
                dateOfBirth: formData.dateOfBirth,
                image: formData.image,
            };

            setUser(updatedUser);
            setIsEditing(false);
        } catch (err) {
            console.error('Error updating user:', err);
        }
    };

    const handleImageSave = async () => {
        if (!selectedFile) return;

        const formDataImage = new FormData();
        formDataImage.append('image', selectedFile);

        const token = localStorage.getItem('token');
        if (!token) {
            console.warn('No auth token found');
            return;
        }

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_URL}/users/${formData.id}/uploadImage`,
                {
                    method: 'POST',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    body: formDataImage,
                }
            );

            if (!response.ok) {
                const errorMsg = await response.text();
                console.error('Error uploading image:', errorMsg);
                return;
            }

            const resultMessage = await response.text();
            console.log('Image upload success:', resultMessage);

            const reader = new FileReader();
            reader.onload = () => {
                const base64Str = (reader.result as string).split(',')[1];

                if (!user || !user.id || !user.email || !user.username || !user.role || !user.dateOfBirth || !user.createdAt || !user.updatedAt) {
                    console.error('Incomplete user data');
                    return;
                }

                const updatedUser = {
                    id: user.id,
                    email: user.email,
                    username: user.username,
                    role: user.role,
                    bio: user.bio || '',
                    dateOfBirth: user.dateOfBirth,
                    createdAt: user.createdAt,
                    updatedAt: user.updatedAt,
                    password: '',
                    image: base64Str,
                };

                setUser(updatedUser);
            };
            reader.readAsDataURL(selectedFile);

            setImageChanged(false);
        } catch (err) {
            console.error('Error uploading image:', err);
        }
    };

    const toggleEdit = () => {
        setError("");
        setIsEditing(!isEditing);
    };

    if (!user)
        return <div className="profile-page">Please log in to view your profile.</div>;

    return (
        <div className="profile-page">
            <div className="profile-header animate-scalein">
                <div className="profile-pic-wrapper">
                    <img
                        src={
                            user.image
                                ? `data:image/jpeg;base64,${user.image}`
                                : 'https://www.gravatar.com/avatar/?d=identicon'
                        }
                        alt={user.username}
                        className="profile-image"
                    />
                </div>
                <div className="profile-info-wrapper">
                    <div className="profile-top-row">
                        <h2 className="profile-username">{user.username}</h2>
                        {!isEditing && (
                            <button className="edit-profile-btn" onClick={toggleEdit}>
                                Edit Profile
                            </button>
                        )}
                    </div>
                    <div className="profile-meta">
                        <p className="profile-meta-item">
                            <strong>Date of Birth:</strong>{' '}
                            {user.dateOfBirth
                                ? new Date(user.dateOfBirth).toLocaleDateString()
                                : 'N/A'}
                        </p>
                        <p className="profile-meta-item">
                            <strong>Email:</strong> {user.email}
                        </p>
                        <p className="profile-meta-item">
                            <strong>Bio:</strong> {user.bio || 'No bio set yet.'}
                        </p>
                    </div>
                </div>
            </div>

            {isEditing && (
                <div className="edit-profile-container animate-fadein">
                    <div className="edit-info-card animate-scalein card-hover">
                        <form onSubmit={handleSubmit}>
                            {error && <div className="error-message">{error}</div>}
                            <div className="edit-field">
                                <label>Username:</label>
                                <input
                                    type="text"
                                    name="username"
                                    value={formData.username}
                                    onChange={handleChange}
                                    placeholder="Enter your username"
                                />
                            </div>
                            <div className="edit-field">
                                <label>Email:</label>
                                <input
                                    type="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    placeholder="Enter your email"
                                />
                            </div>
                            <div className="edit-field">
                                <label>Bio:</label>
                                <textarea
                                    name="bio"
                                    value={formData.bio}
                                    onChange={handleChange}
                                    placeholder="Write something about yourself"
                                />
                            </div>
                            <div className="edit-field">
                                <label>Parola actuală/O nouă parolă:</label>
                                <input
                                    type="password"
                                    name="password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    placeholder="New password"
                                />
                            </div>
                            <button type="submit" className="save-changes-btn">
                                Save Changes
                            </button>
                            <button type="button" className="cancel-btn" onClick={toggleEdit}>
                                Cancel
                            </button>
                        </form>
                    </div>
                    <div className="edit-image-card animate-scalein card-hover">
                        <div className="edit-field">
                            <label>Profile Image:</label>
                            <input
                                type="file"
                                name="image"
                                onChange={handleFileChange}
                                title="Choose a profile image to upload"
                            />
                        </div>
                        {selectedFile && imageChanged && (
                            <button type="button" onClick={handleImageSave}>
                                Save Image
                            </button>
                        )}
                    </div>
                </div>
            )}

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
                <div className="tab-content animate-fadein">
                    {activeTab === 'Postări' && (
                        <UserPosts userId={user.id} token={localStorage.getItem('token') || ''}/>
                    )}

                    {activeTab === 'Albume' && (
                        <AlbumGallery userId={user.id} token={localStorage.getItem('token') || ''}/>

                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;