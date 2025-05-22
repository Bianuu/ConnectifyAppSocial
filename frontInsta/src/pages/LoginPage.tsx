import React, {useEffect, useState} from 'react';
import './LoginPage.scss';
import {Link, useNavigate} from 'react-router-dom';
import {useUserContext} from '../context/UserContext';
import {jwtDecode} from 'jwt-decode';


type DecodedToken = {
    id: string;
    email: string;
    username: string;
    role: string;
    bio?: string;
    image?: any;
    dateOfBirth?: string;
    createdAt?: string;
    updatedAt?: string;
    exp: number;
    iat: number;
};

const LoginPage: React.FC = () => {
    const navigate = useNavigate();
    const {setUser} = useUserContext();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loginAttempts, setLoginAttempts] = useState(0);
    const [isCooldown, setIsCooldown] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (loginAttempts >= 3) {
            setIsCooldown(true);
            setError('Too many attempts. Please wait 10 seconds.');

            const timeout = setTimeout(() => {
                setLoginAttempts(0);
                setIsCooldown(false);
                setError('');
            }, 10000);

            return () => clearTimeout(timeout);
        }
    }, [loginAttempts]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (isCooldown) return;

        if (!email || !password) {
            setError('Both fields are required!');
            return;
        }

        try {
            setIsLoading(true);
            const response = await fetch(`${import.meta.env.VITE_API_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({email, password}),
            });

            if (response.ok) {
                const data = await response.json();
                const decoded: DecodedToken = jwtDecode(data.token);

                localStorage.setItem('token', data.token);

                setUser({
                    id: decoded.id,
                    email: decoded.email,
                    username: decoded.username,
                    role: decoded.role,
                    bio: decoded.bio ?? '',
                    image: decoded.image ?? null,
                    dateOfBirth: decoded.dateOfBirth ? new Date(decoded.dateOfBirth).toISOString() : '',
                    createdAt: decoded.createdAt ? new Date(decoded.createdAt).toISOString() : '',
                    updatedAt: decoded.updatedAt ? new Date(decoded.updatedAt).toISOString() : '',
                });

                if (decoded.role === 'ADMIN') {
                    navigate('/admin/manage');
                } else {
                    navigate('/home');
                }
            } else {
                const errorData = await response.json().catch(() => ({}));
                setLoginAttempts(prev => prev + 1);
                setError(errorData.message || 'Invalid credentials, please try again.');
            }
        } catch (err) {
            console.error('Error during login:', err);
            setError('Something went wrong, please try again later.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <div className="logo"></div>
                <form onSubmit={handleSubmit}>
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    {error && <div className="error-message">{error}</div>}
                    <button type="submit" disabled={isCooldown}>
                        {isCooldown ? 'Please wait...' : 'Log In'}
                    </button>
                </form>
            </div>
            <div className="divider">OR</div>
            <Link className="forgot-password" to="/forgot-password">Forgot password?</Link>
            <div className="signup-link">
                Don't have an account? <a href="/signup">Sign up</a>
            </div>
        </div>
    );
};

export default LoginPage;