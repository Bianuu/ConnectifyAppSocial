import React, {useState} from 'react';
import './SignUpPage.scss';
import {useNavigate} from 'react-router-dom';

const SignUpPage: React.FC = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        email: '',
        username: '',
        password: '',
        bio: '',
        dateOfBirth: '',
        image: null as File | null,
    });

    const [passwordError, setPasswordError] = useState('');
    const [dobError, setDobError] = useState('');

    const calculateAge = (dob: string): number => {
        const birthDate = new Date(dob);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const {name, value} = e.target;

        if (name === 'password') {
            if (passwordError && value.length >= 7 && value.length <= 12) {
                setPasswordError('');
            }
        }

        if (name === 'dateOfBirth') {
            const age = calculateAge(value);
            if (dobError && age >= 14) {
                setDobError('');
            }
        }

        setFormData(prev => ({...prev, [name]: value}));
    };

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setFormData(prev => ({...prev, image: e.target.files![0]}));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (formData.password.length < 7 || formData.password.length > 12) {
            setPasswordError('Password must be between 7 and 12 characters');
            return;
        }

        const age = calculateAge(formData.dateOfBirth);
        if (age < 14) {
            setDobError('You must be at least 14 years old');
            return;
        }

        const toBase64 = (file: File): Promise<string> =>
            new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onloadend = () => resolve((reader.result as string).split(',')[1]);
                reader.onerror = reject;
                reader.readAsDataURL(file);
            });

        let imageBase64: string | null = null;
        if (formData.image) {
            imageBase64 = await toBase64(formData.image);
        }

        const payload = {
            email: formData.email,
            username: formData.username,
            password: formData.password,
            bio: formData.bio,
            role: 'USER',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            dateOfBirth: formData.dateOfBirth,
            image: imageBase64 || null,
        };


        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/auth/signup`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                const error = await res.json();
                alert(`Error: ${error.message || 'Something went wrong'}`);
                return;
            }

            alert('Account created successfully! You can now log in.');
            navigate('/login');
        } catch (err) {
            console.error('Registration failed:', err);
            alert('Registration failed');
        }
    };

    return (
        <div className="signup-wrapper">
            <div className="signup-card">
                <h1 className="logo"></h1>
                <p className="subtitle">Sign up to see photos and videos from your friends.</p>
                <form onSubmit={handleSubmit}>

                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        name="username"
                        type="text"
                        placeholder="Username"
                        required
                        onChange={handleChange}
                    />

                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        placeholder="Email"
                        required
                        onChange={handleChange}
                    />

                    <label htmlFor="password">Password</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        placeholder="Password"
                        required
                        onChange={handleChange}
                        className={passwordError ? 'input-error' : ''}
                    />
                    {passwordError && <p className="error-message">{passwordError}</p>}

                    <label htmlFor="bio">Short Bio</label>
                    <textarea
                        id="bio"
                        name="bio"
                        placeholder="Short bio"
                        onChange={handleChange}
                        rows={5}
                        maxLength={200}
                    />

                    <label htmlFor="dateOfBirth">Date of Birth</label>
                    <input
                        id="dateOfBirth"
                        name="dateOfBirth"
                        type="date"
                        required
                        onChange={handleChange}
                        className={dobError ? 'input-error' : ''}
                    />
                    {dobError && <p className="error-message">{dobError}</p>}

                    <label htmlFor="image">Upload Profile Image</label>
                    <input
                        id="image"
                        name="image"
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                    />

                    <button type="submit">Sign Up</button>
                </form>

                <p className="hint" onClick={() => navigate('/login')}>
                    Already have an account? <span>Log in instead.</span>
                </p>
            </div>
        </div>
    );
};

export default SignUpPage;
