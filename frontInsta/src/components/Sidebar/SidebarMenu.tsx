import React, {useEffect} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import './SidebarMenu.scss';
import {useUserContext} from '../../context/UserContext';

const SidebarMenu: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const {user} = useUserContext();

    const isAdmin = user?.role === 'ADMIN';

    if (
        location.pathname === '/forgot-password' ||
        location.pathname.startsWith('/reset-password')
    ) {
        return null;
    }


    useEffect(() => {
        const alreadyReloaded = sessionStorage.getItem('adminReloaded');

        // Dacă e admin, nu am dat încă refresh, și nu e deja pe /admin/manage
        if (isAdmin && !alreadyReloaded && location.pathname !== '/admin/manage') {
            sessionStorage.setItem('adminReloaded', 'true'); // Marcăm că am dat refresh
            window.location.href = '/admin/manage';
        }
    }, [isAdmin, location.pathname, navigate]);

    return (
        <div className="sidebar-menu">
            <div className="menu-section">
                <p className="section-label"></p>

                {isAdmin && (
                    <button
                        className={location.pathname === '/searcha' ? 'active' : ''}
                        onClick={() => navigate('/searcha')}
                    >
                        Search profile
                    </button>
                )}

                {!isAdmin && (
                    <>
                        <button
                            className={location.pathname === '/home' ? 'active' : ''}
                            onClick={() => navigate('/home')}
                        >
                            Home
                        </button>
                        <button
                            className={location.pathname === '/friends' ? 'active' : ''}
                            onClick={() => navigate('/friends')}
                        >
                            Friends
                        </button>
                        <button
                            className={location.pathname === '/profile' ? 'active' : ''}
                            onClick={() => navigate('/profile')}
                        >
                            My Profile
                        </button>
                        <button
                            className={location.pathname === '/search' ? 'active' : ''}
                            onClick={() => navigate('/search')}
                        >
                            Search profile
                        </button>
                        <button
                            className={location.pathname === '/searchh' ? 'active' : ''}
                            onClick={() => navigate('/searchh')}
                        >
                            Conversations
                        </button>
                    </>
                )}
            </div>

            {isAdmin && (
                <div className="menu-section">
                    <p className="section-label">Platform Administration</p>
                    <button
                        className={location.pathname === '/admin/manage' ? 'active' : ''}
                        onClick={() => navigate('/admin/manage')}
                    >
                        Accounts Management
                    </button>
                </div>
            )}
        </div>
    );
};

export default SidebarMenu;
