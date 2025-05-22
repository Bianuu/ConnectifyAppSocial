import React, { useEffect, useState } from 'react';
import './CommentSection.scss';
import { useUserContext } from '../../context/UserContext';

interface Comment {
    id: string;
    postId: string;
    userId: string;
    content: string;
    createdAt: string;
    username: string;
    postAuthor: string;
}

interface CommentSectionProps {
    postId: string;
    userId: string;
    token: string;
    hideInput?: boolean; // 👈 ADĂUGAT
}

const CommentSection: React.FC<CommentSectionProps> = ({ postId, userId, token, hideInput = false }) => {
    const { user: currentUser } = useUserContext();

    const [comments, setComments] = useState<Comment[]>([]);
    const [newComment, setNewComment] = useState('');
    const [loading, setLoading] = useState(false);

    const fetchComments = async () => {
        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/comments/allP/${postId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (res.ok) {
                const data = await res.json();
                setComments(data);
            } else {
                console.error('Failed to fetch comments');
            }
        } catch (err) {
            console.error('Error fetching comments:', err);
        }
    };

    const handleAddComment = async () => {
        if (!newComment.trim()) return;

        setLoading(true);
        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/comments/insert`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    userId,
                    postId,
                    content: newComment,
                    createdAt: new Date().toISOString(),
                }),
            });

            if (res.ok) {
                setNewComment('');
                await fetchComments();
            } else {
                console.error('Failed to post comment');
            }
        } catch (err) {
            console.error('Error posting comment:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchComments();
    }, [postId]);

    return (
        <div className="comment-section">
            <h4 className="comment-section__title">Comments</h4>

            <div className="comment-section__list">
                {comments.map((comment) => (
                    <div key={comment.id} className="comment">
                        <div className="comment__content">{comment.content}</div>
                        <div className="comment__date">
                            <span>Added by </span>
                            <span className="comment__author">{comment.username}</span>
                            <span> at </span>
                            <span className="comment__timestamp">{new Date(comment.createdAt).toLocaleString()}</span>
                        </div>
                    </div>
                ))}
            </div>

            {/* ✅ Ascundem inputul pentru comentarii dacă e ADMIN */}
            {!hideInput && (
                <div className="comment-section__input">
                    <input
                        type="text"
                        placeholder="Write a comment..."
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        disabled={loading}
                    />
                    <button onClick={handleAddComment} disabled={loading || !newComment.trim()}>
                        Post
                    </button>
                </div>
            )}
        </div>
    );
};

export default CommentSection;
