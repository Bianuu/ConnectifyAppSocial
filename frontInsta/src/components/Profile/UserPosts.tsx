import React, { useEffect, useState } from 'react';
import { Edit, Trash2 } from "lucide-react";
import { jwtDecode } from 'jwt-decode';
import './UserPosts.scss';
import LikeButton from './LikeButton';
import CommentSection from './CommentSection';

interface Post {
    id: string;
    content: string;
    image?: string;
    createdAt: string;
    updatedAt: string;
    likesCount: number;
    userId: string;
    postAuthor: string;
}

interface UserPostsProps {
    userId: string;
    token: string;
}

interface DecodedToken {
    id: string;
    userId: string;
    role: string;
}

const UserPosts: React.FC<UserPostsProps> = ({ userId, token }) => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [content, setContent] = useState('');
    const [image, setImage] = useState<File | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [modalImage, setModalImage] = useState<string | null>(null);

    const [deletePostId, setDeletePostId] = useState<string | null>(null);
    const [updatePostId, setUpdatePostId] = useState<string | null>(null);
    const [updateContent, setUpdateContent] = useState('');

    const [userRole, setUserRole] = useState('');
    const [loggedInUserId, setLoggedInUserId] = useState('');

    useEffect(() => {
        try {
            const decoded: DecodedToken = jwtDecode(token);
            setLoggedInUserId(decoded.id);
            setUserRole(decoded.role);
        } catch (err) {
            console.error('Token decoding failed', err);
        }
    }, [token]);

    const fetchPosts = async () => {
        try {
            setLoading(true);
            const res = await fetch(`${import.meta.env.VITE_API_URL}/posts/all/${userId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (res.status === 500) {
                console.warn('Backend returned 500 - no posts for this user.');
                setPosts([]);
                return;
            }

            if (!res.ok) throw new Error('Failed to fetch posts');

            const data = await res.json();
            setPosts(data);
        } catch (err) {
            console.error(err);
            setError('Eroare la încărcarea postărilor.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const fetchAll = async () => {
            await fetchPosts();
        };
        fetchAll();
    }, [userId, token]);

    useEffect(() => {
        const fetchLikesForPosts = async () => {
            if (posts.length === 0) return;

            const updated = await Promise.all(posts.map(async (post) => {
                try {
                    const res = await fetch(`${import.meta.env.VITE_API_URL}/likes/count/${post.id}`, {
                        headers: { Authorization: `Bearer ${token}` },
                    });
                    const count = res.ok ? await res.json() : 0;
                    return { ...post, likesCount: count };
                } catch {
                    return { ...post, likesCount: 0 };
                }
            }));
            setPosts(updated);
        };

        fetchLikesForPosts();
    }, [posts]);

    const handleAddPost = async () => {
        if (!content.trim()) {
            setError('Conținutul postării este obligatoriu.');
            return;
        }

        const formData = new FormData();
        formData.append('content', content);
        formData.append('userId', userId);
        if (image) formData.append('image', image);

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/posts/insert`, {
                method: 'POST',
                headers: { Authorization: `Bearer ${token}` },
                body: formData,
            });

            if (!res.ok) {
                const errorData = await res.json();
                setError(errorData?.error || 'Eroare la adăugarea postării.');
                return;
            }

            setContent('');
            setImage(null);
            setError('');
            await fetchPosts();
        } catch (err) {
            console.error(err);
            setError('Eroare la rețea.');
        }
    };

    const handleDeletePost = async () => {
        if (!deletePostId) return;

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/posts/delete/${deletePostId}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) throw new Error('Failed to delete post');
            setDeletePostId(null);
            await fetchPosts();
        } catch (err) {
            console.error(err);
            setError('Eroare la ștergerea postării.');
        }
    };

    const handleUpdatePost = async () => {
        if (!updatePostId || !updateContent.trim()) {
            setError('Conținutul postării este obligatoriu.');
            return;
        }

        const currentPost = posts.find(post => post.id === updatePostId);
        if (!currentPost) {
            setError('Postarea nu a fost găsită.');
            return;
        }

        const updatedPost = {
            id: updatePostId,
            content: updateContent,
            userId: currentPost.userId,
            image: currentPost.image,
            likesCount: currentPost.likesCount,
            createdAt: currentPost.createdAt,
            updatedAt: new Date().toISOString()
        };

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/posts/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(updatedPost),
            });

            if (!res.ok) {
                const errorData = await res.json();
                setError(errorData?.error || 'Eroare la actualizarea postării.');
                return;
            }

            setUpdatePostId(null);
            setUpdateContent('');
            setError('');
            await fetchPosts();
        } catch (err) {
            console.error(err);
            setError('Eroare la rețea.');
        }
    };

    const openUpdateModal = (post: Post) => {
        setUpdatePostId(post.id);
        setUpdateContent(post.content);
    };

    useEffect(() => {
        if (error) {
            const timer = setTimeout(() => setError(''), 5000);
            return () => clearTimeout(timer);
        }
    }, [error]);

    const handleLikeToggle = (postId: string, change: number) => {
        setPosts(prev =>
            prev.map(post =>
                post.id === postId
                    ? { ...post, likesCount: post.likesCount + change }
                    : post
            )
        );
    };

    if (!loggedInUserId) {
        return <p>Se inițializează utilizatorul...</p>;
    }

    return (
        <div className="user-posts">
            <h3>Postările</h3>
            {error && <p className="error-message">{error}</p>}

            {userId === loggedInUserId && userRole !== 'ADMIN' && (
                <div className="add-post-form">
                    <textarea
                        placeholder="Scrie o postare..."
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    />
                    <input
                        type="file"
                        onChange={(e) => setImage(e.target.files?.[0] || null)}
                        accept="image/*"
                    />
                    <button onClick={handleAddPost}>Adaugă postare</button>
                </div>
            )}

            {loading ? (
                <p>Se încarcă postările...</p>
            ) : posts.length === 0 ? (
                <div className="empty-posts">Nu există nicio postare!</div>
            ) : (
                <div className="posts-list">
                    {posts.map((post) => (
                        <div key={post.id} className="post-card">
                            <p>{post.content}</p>
                            {post.image && (
                                <img
                                    src={`data:image/jpeg;base64,${post.image}`}
                                    alt="Post"
                                    className="post-image"
                                    onClick={() => setModalImage(`data:image/jpeg;base64,${post.image}`)}
                                />
                            )}
                            <small>Postat pe: {new Date(post.createdAt).toLocaleString()}</small>

                            <div className="post-footer">
                                {userRole !== 'ADMIN' && (
                                    <div className="like-section">
                                        <LikeButton
                                            postId={post.id}
                                            userId={loggedInUserId}
                                            token={localStorage.getItem('token') || ''}
                                            onLikeToggle={handleLikeToggle}
                                        />
                                        {post.userId === loggedInUserId && (
                                            <span className="likes-count">{post.likesCount} likes</span>
                                        )}
                                    </div>
                                )}

                                <CommentSection
                                    postId={post.id}
                                    userId={loggedInUserId}
                                    token={localStorage.getItem('token') || ''}
                                    hideInput={userRole === 'ADMIN'}
                                />
                            </div>

                            <div className="post-actions">
                                {loggedInUserId === userId && (
                                    <>
                                        <button
                                            className="update-btn"
                                            onClick={() => openUpdateModal(post)}
                                            aria-label="Edit post"
                                            title="Editează postarea"
                                        >
                                            <Edit size={18} />
                                            <span>Editează</span>
                                        </button>

                                        <button
                                            className="delete-btn"
                                            onClick={() => setDeletePostId(post.id)}
                                            aria-label="Delete post"
                                            title="Șterge postarea"
                                        >
                                            <Trash2 size={18} />
                                            <span>Șterge</span>
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {modalImage && (
                <div className="modal-overlay" onClick={() => setModalImage(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <img src={modalImage} alt="Full post" />
                        <button
                            className="modal-close-btn"
                            onClick={() => setModalImage(null)}
                            aria-label="Close image modal"
                        >
                            ×
                        </button>
                    </div>
                </div>
            )}

            {deletePostId && (
                <div className="modal-overlay" onClick={() => setDeletePostId(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h3>Confirmare ștergere</h3>
                        <p>Sigur vrei să ștergi această postare?</p>
                        <div className="modal-buttons">
                            <button onClick={handleDeletePost} className="confirm-btn">Șterge</button>
                            <button onClick={() => setDeletePostId(null)} className="cancel-btn">Anulează</button>
                        </div>
                    </div>
                </div>
            )}

            {updatePostId && (
                <div className="modal-overlay" onClick={() => setUpdatePostId(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h3>Editează postarea</h3>
                        <textarea
                            value={updateContent}
                            onChange={(e) => setUpdateContent(e.target.value)}
                            rows={5}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleUpdatePost} className="confirm-btn">Salvează</button>
                            <button onClick={() => setUpdatePostId(null)} className="cancel-btn">Anulează</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserPosts;
