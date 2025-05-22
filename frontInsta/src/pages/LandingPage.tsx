import React from 'react';
import './LandingPage.scss';
import {useNavigate} from 'react-router-dom';

const LandingPage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="landing-wrapper">
            {/* Cardul central cu logo, nume aplicație și butoane pentru acțiuni */}
            <div className="landing-content">

                <h1 className="app-name"></h1>
                <p className="tagline">Fii tu schimbarea. Conectează-te, distribuie și inspiră!</p>
                <div className="auth-card">
                    <button className="login-btn" onClick={() => navigate('/login')}>
                        Conectează-te
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LandingPage;
