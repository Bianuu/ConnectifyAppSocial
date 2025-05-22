import React, {useEffect, useState} from "react";
import {Trash2} from "lucide-react";
import {jwtDecode} from "jwt-decode";
import "./AccountsManagementPage.scss";

interface User {
    id: string;
    username: string;
    email: string;
}

interface Comment {
    id: string;
    content: string;
    username: string;
    createdAt: string;
}

interface DecodedToken {
    role: string;
    exp: number;
}

interface Post {
    id: string;
    content: string;
    image?: string;
    createdAt: string;
    userId: string;

}


const AccountManagementPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [userToDelete, setUserToDelete] = useState<User | null>(null);
    const [comments, setComments] = useState<Comment[]>([]);
    const [commentToDelete, setCommentToDelete] = useState<Comment | null>(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [isAdmin, setIsAdmin] = useState<boolean | null>(null);
    const [posts, setPosts] = useState<Post[]>([]);
    const [postToDelete, setPostToDelete] = useState<Post | null>(null);
    const [modalImage, setModalImage] = useState<string | null>(null);


    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token) {
            setIsAdmin(false);
            return;
        }

        try {
            const decoded: DecodedToken = jwtDecode(token);
            if (decoded.role === "ADMIN") {
                setIsAdmin(true);
            } else {
                setIsAdmin(false);
            }
        } catch (err) {
            console.error("Failed to decode token:", err);
            setIsAdmin(false);
        }
    }, [token]);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await fetch(`${import.meta.env.VITE_API_URL}/users/all`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to fetch users");
            const data: User[] = await response.json();
            const filteredUsers = data.filter((user) => (user as any).role !== "ADMIN");
            setUsers(filteredUsers);
        } catch (err) {
            setError("Failed to load users.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const deleteUser = async (id: string) => {
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/users/delete/${id}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to delete user");
            setUsers((prev) => prev.filter((user) => user.id !== id));
        } catch (err) {
            setError("Failed to delete user.");
            console.error(err);
        }
    };
    const fetchPosts = async () => {
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/posts/all`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to fetch posts");
            const data: Post[] = await response.json();
            setPosts(data);
        } catch (err) {
            console.error("Failed to load posts.", err);
        }
    };
    const deletePost = async (id: string) => {
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/posts/delete/${id}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to delete post");
            setPosts((prev) => prev.filter((post) => post.id !== id));
        } catch (err) {
            console.error("Failed to delete post.", err);
        }
    };

    const fetchComments = async () => {
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/comments/all`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to fetch comments");
            const data: Comment[] = await response.json();
            setComments(data);
        } catch (err) {
            console.error("Failed to load comments.", err);
        }
    };

    const deleteComment = async (id: string) => {
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/comments/delete/${id}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) throw new Error("Failed to delete comment");
            setComments((prev) => prev.filter((comment) => comment.id !== id));
        } catch (err) {
            console.error("Failed to delete comment.", err);
        }
    };

    const confirmDeleteUser = () => {
        if (userToDelete) {
            deleteUser(userToDelete.id);
            setUserToDelete(null);
        }
    };

    const confirmDeleteComment = () => {
        if (commentToDelete) {
            deleteComment(commentToDelete.id);
            setCommentToDelete(null);
        }
    };

    useEffect(() => {
        if (isAdmin) {
            // Doar pentru un singur refresh automat
            if (!sessionStorage.getItem("refreshed")) {
                sessionStorage.setItem("refreshed", "true");
                window.location.reload();
            } else {
                fetchUsers();
                fetchComments();
                fetchPosts();
            }
        }
    }, [isAdmin]);


    if (isAdmin === null) {
        return <p>Checking access...</p>;
    }

    return (
        <div className="account-management">
            <h2>Accounts Management</h2>

            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <p>Loading users...</p>
            ) : (
                <>
                    <table>
                        <thead>
                        <tr>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map((user) => (
                            <tr key={user.id}>
                                <td>{user.username}</td>
                                <td>{user.email}</td>
                                <td>
                                    <button
                                        className="delete-btn"
                                        onClick={() => setUserToDelete(user)}
                                        title="Delete user"
                                    >
                                        <Trash2 size={20} color="#d11a2a"/>
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <h3>User Comments</h3>
                    <table className="comments-table">
                        <thead>
                        <tr>
                            <th>Username</th>
                            <th>Content</th>
                            <th>Post Author</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {comments.map((comment) => (
                            <tr key={comment.id}>
                                <td>{comment.username}</td>
                                <td>{comment.content}</td>
                                <td>{comment.postAuthor}</td>
                                <td>{new Date(comment.createdAt).toLocaleString()}</td>
                                <td>
                                    <button
                                        className="delete-btn"
                                        onClick={() => setCommentToDelete(comment)}
                                        title="Delete comment"
                                    >
                                        <Trash2 size={20} color="#d11a2a"/>
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <h3>User Posts</h3>
                    <table className="posts-table">
                        <thead>
                        <tr>
                            <th>Content</th>
                            <th>Image</th>
                            <th>Post Author</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {posts.map((post) => (
                            <tr key={post.id}>
                                <td>{post.content}</td>
                                <td>
                                    {post.image ? (
                                        <img
                                            src={`data:image/jpeg;base64,${post.image}`}
                                            alt="Post"
                                            className="cm-image"
                                            onClick={() => setModalImage(`data:image/jpeg;base64,${post.image}`)}
                                        />
                                    ) : (
                                        <em>No image</em>
                                    )}
                                </td>
                                <td>{post.postAuthor}</td>
                                <td>{new Date(post.createdAt).toLocaleString()}</td>
                                <td>
                                    <button
                                        className="delete-btn"
                                        onClick={() => setPostToDelete(post)}
                                        title="Delete post"
                                    >
                                        <Trash2 size={20} color="#d11a2a"/>
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>


                </>
            )}

            {/* Modal delete user */}
            {userToDelete && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Are you sure you want to delete this user?</h3>
                        <p>
                            {userToDelete.username} ({userToDelete.email})
                        </p>
                        <div className="modal-actions">
                            <button className="cancel-btn" onClick={() => setUserToDelete(null)}>
                                Cancel
                            </button>
                            <button className="delete-confirm-btn" onClick={confirmDeleteUser}>
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal delete comment */}
            {commentToDelete && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Are you sure you want to delete this comment?</h3>
                        <p>"{commentToDelete.content}" by {commentToDelete.username}</p>
                        <div className="modal-actions">
                            <button className="cancel-btn" onClick={() => setCommentToDelete(null)}>
                                Cancel
                            </button>
                            <button className="delete-confirm-btn" onClick={confirmDeleteComment}>
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {modalImage && (
                <div className="modal-overlay" onClick={() => setModalImage(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <img src={modalImage} alt="Zoomed post" className="modal-full-image"/>
                        <button className="modal-close-btn" onClick={() => setModalImage(null)}>×</button>
                    </div>
                </div>
            )}

            {postToDelete && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Are you sure you want to delete this post?</h3>
                        <p>"{postToDelete.content}" by {postToDelete.postAuthor}</p>
                        <div className="modal-actions">
                            <button className="cancel-btn" onClick={() => setPostToDelete(null)}>Cancel</button>
                            <button className="delete-confirm-btn" onClick={() => {
                                deletePost(postToDelete.id);
                                setPostToDelete(null);
                            }}>Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
};

export default AccountManagementPage;
