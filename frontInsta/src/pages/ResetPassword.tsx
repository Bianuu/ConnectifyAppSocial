import {useNavigate, useParams} from 'react-router-dom';
import {useEffect, useState} from 'react';
import axios from 'axios';
import './ResetPassword.scss';

function ResetPassword() {
    const {userId} = useParams();
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        console.log("User ID from URL:", userId);
    }, [userId]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axios.put(`http://localhost:8080/users/updatee/${userId}/${password}`);
            setMessage('Parola a fost resetată cu succes!');
            setTimeout(() => navigate('/login'), 2000); // Redirect la login după 2 secunde
        } catch (error) {
            console.error('Eroare la resetare parolă:', error);
            setMessage('Eroare la resetarea parolei.');
        }
    };

    return (
        <div className="reset-password-container">
            <h1>Resetare parolă</h1>
            <form onSubmit={handleSubmit}>
                <label>
                    Noua parolă:
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </label>
                <button type="submit">Resetare parolă</button>
            </form>
            {message && <p className={message.includes('succes') ? '' : 'error'}>{message}</p>}
        </div>
    );
}

export default ResetPassword;