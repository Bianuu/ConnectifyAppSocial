import React, {useEffect, useState} from 'react';
import './LikeButton.scss';

interface LikeButtonProps {
    postId: string;
    userId: string;
    token: string;
    onLikeToggle?: (postId: string, change: number) => void;
}

const LikeButton: React.FC<LikeButtonProps> = ({postId, userId, token, onLikeToggle}) => {
    const [liked, setLiked] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        const checkIfLiked = async () => {
            try {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/likes/user/${userId}/post/${postId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (res.ok) {
                    const hasLiked = await res.json();
                    setLiked(hasLiked);
                } else {
                    console.error('Failed to check like status');
                }
            } catch (err) {
                console.error('Error checking like status:', err);
            }
        };

        checkIfLiked();
    }, [postId, userId, token]);

    const handleLikeToggle = async () => {
        setLoading(true);
        try {
            if (liked) {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/likes/delete/${postId}/${userId}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (res.ok) {
                    setLiked(false);
                    onLikeToggle?.(postId, -1);
                } else console.error('Failed to unlike');
            } else {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/likes/insert`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify({
                        userId,
                        postId,
                        createdAt: new Date().toISOString(),
                    }),
                });
                if (res.ok) {
                    setLiked(true);
                    onLikeToggle?.(postId, 1);
                } else console.error('Failed to like');
            }
        } catch (err) {
            console.error('Error toggling like:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <button
            onClick={handleLikeToggle}
            disabled={loading}
            className={`like-button ${liked ? 'liked' : ''}`}
        >
            {liked ? '💖 Dislike' : '🤍 Like'}
        </button>
    );
};

export default LikeButton;
