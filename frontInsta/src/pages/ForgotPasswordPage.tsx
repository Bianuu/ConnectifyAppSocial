import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import './ForgotPasswordPage.scss';

const ForgotPasswordPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [status, setStatus] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setStatus(null);

        if (!email.includes('@')) {
            setStatus('Adresa email este invalidă.');
            return;
        }

        try {
            const res = await fetch(`http://localhost:8080/users/reset-password/${encodeURIComponent(email)}`, {
                method: 'POST',
            });

            if (res.ok) {
                setStatus('Email trimis cu succes!');
                setEmail('');
            } else {
                setStatus('Eroare la trimitere.');
            }
        } catch {
            setStatus('Eroare la conexiune cu serverul.');
        }
    };

    return (
        <div className="forgot-password-wrapper">
            <form className="forgot-password-form" onSubmit={handleSubmit}>
                <h2>Resetare parolă</h2>
                <input
                    type="email"
                    name="email"
                    placeholder="Adresa email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <button type="submit">Trimite email</button>
                {status && (
                    <p
                        className={`status ${
                            status === 'Email trimis cu succes!' ? 'success' : ''
                        }`}
                    >
                        {status}
                    </p>
                )}
                <p style={{fontSize: '0.85rem'}}>
                    Îți amintești parola? <Link to="/">Conectează-te</Link>
                </p>
            </form>
        </div>
    );
};

export default ForgotPasswordPage;
