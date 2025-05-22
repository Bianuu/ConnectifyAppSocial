import React, {useEffect, useState} from 'react';
import {useUserContext} from '../../context/UserContext';
import './HomePage.scss';
import LikeButton from '../Profile/LikeButton';
import CommentSection from '../Profile/CommentSection';
import SuggestedFriends from '../FriendSuggestions/SuggestedFriends';

interface PostDTO {
    id: string;
    userId: string;
    content: string;
    image?: string;
    likesCount: number;
    createdAt: string;
    updatedAt: string;
}

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

const HomePage: React.FC = () => {
    const {user: currentUser} = useUserContext();
    const [posts, setPosts] = useState<PostDTO[]>([]);
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchNewsFeed = async () => {
            try {
                const token = localStorage.getItem('token');
                if (!token || !currentUser) throw new Error('No user or token');

                const [friendsRes, usersRes] = await Promise.all([
                    fetch(`${import.meta.env.VITE_API_URL}/friends/all`, {
                        headers: {Authorization: `Bearer ${token}`},
                    }),
                    fetch(`${import.meta.env.VITE_API_URL}/users/all`, {
                        headers: {Authorization: `Bearer ${token}`},
                    }),
                ]);

                const friendsData: FriendDTO[] = await friendsRes.json();
                const usersData: User[] = await usersRes.json();

                setUsers(usersData);

                const friendIds = friendsData
                    .filter(f =>
                        f.user1Id === currentUser.id || f.user2Id === currentUser.id
                    )
                    .map(f =>
                        f.user1Id === currentUser.id ? f.user2Id : f.user1Id
                    );

                const postsResponses = await Promise.all(
                    friendIds.map(friendId =>
                        fetch(`${import.meta.env.VITE_API_URL}/posts/all/${friendId}`, {
                            headers: {Authorization: `Bearer ${token}`},
                        })
                    )
                );

                const allPostsArrays = await Promise.all(
                    postsResponses
                        .filter(res => res.ok)
                        .map(res => res.json())
                );
                const mergedPosts: PostDTO[] = allPostsArrays.flat();

                const sortedPosts = mergedPosts.sort(
                    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                );

                setPosts(sortedPosts);
            } catch (err) {
                console.error('Failed to fetch news feed:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchNewsFeed();
    }, [currentUser]);

    const getUserById = (id: string) => users.find(u => u.id === id);

    const handleLikeToggle = (postId: string, change: number) => {
        setPosts(prev =>
            prev.map(post =>
                post.id === postId
                    ? {...post, likesCount: post.likesCount + change}
                    : post
            )
        );
    };

    return (
        <div className="home-page">
            <h1>Newsfeed for user {currentUser?.username}</h1>
            <SuggestedFriends/>
            <br></br>
            {loading ? (
                <p>Loading...</p>
            ) : posts.length === 0 ? (
                <p>No posts to show.</p>
            ) : (
                <ul className="post-list">
                    {posts.map(post => {
                        const author = getUserById(post.userId);
                        return (
                            <li key={post.id} className="post-card">
                                <div className="post-header">
                                    <img
                                        src={
                                            author?.image
                                                ? `data:image/jpeg;base64,${author.image}`
                                                : 'https://www.gravatar.com/avatar/?d=identicon'
                                        }
                                        alt={author?.username}
                                        className="avatar"
                                    />
                                    <span className="username">{author?.username}</span>
                                    <span className="date">{new Date(post.createdAt).toLocaleString()}</span>
                                </div>

                                <div className="post-body">
                                    <p>{post.content}</p>
                                    {post.image && (
                                        <img
                                            src={`data:image/jpeg;base64,${post.image}`}
                                            alt="Post"
                                            className="post-image"
                                        />
                                    )}
                                </div>

                                <div className="post-footer">
                                    {currentUser && (
                                        <>
                                            <div className="like-section">
                                                <LikeButton
                                                    postId={post.id}
                                                    userId={currentUser.id}
                                                    token={localStorage.getItem('token') || ''}
                                                    onLikeToggle={handleLikeToggle}
                                                />
                                                {/*<span className="likes-count">{post.likesCount} likes</span>*/}
                                            </div>

                                            <CommentSection
                                                postId={post.id}
                                                userId={currentUser.id}
                                                token={localStorage.getItem('token') || ''}
                                            />
                                        </>
                                    )}
                                </div>

                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    );
};

export default HomePage;
