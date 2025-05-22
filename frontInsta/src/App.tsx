import './App.css';
import Header from './components/Header/Header';
import SidebarMenu from './components/Sidebar/SidebarMenu';
import {Navigate, Route, Routes, useLocation} from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import ProfilePage from './components/Profile/PageProfile';
import SearchPage from './components/SearchPage/SearchPage';
import UserProfilePage from './components/UserProfile/UserProfilePage';
import FriendsPage from './components/Friends/FriendsPage';
import AccountsManagementPage from './components/Admin/AccountsManagement/AccountsManagementPage';
import HomePage from './components/Home/HomePage';
import ChatPage from './components/Chat/ChatPage';
import SearchChat from './components/SearchChat/SearchChat';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import SearchPageAdmin from "./components/SearchPageAdmin/SearchPageAdmin.tsx";
import ResetPassword from "./pages/ResetPassword.tsx";
import {useEffect, useState} from 'react';
import {jwtDecode} from 'jwt-decode';
import {useUserContext} from './context/UserContext';

interface DecodedToken {
    role: string;
}

function App() {
    const location = useLocation();
    const {user, loading} = useUserContext();
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            setIsAdmin(false);
            return;
        }

        try {
            const decoded: DecodedToken = jwtDecode(token);
            setIsAdmin(decoded.role === 'ADMIN');
        } catch (err) {
            console.error('Failed to decode token:', err);
            setIsAdmin(false);
        }
    }, []);

    // Listează toate rutele publice
    const publicPaths = [
        '/',
        '/login',
        '/signup',
        '/forgot-password',
        '/reset-password/'
    ];

    // Verifică dacă ruta curentă este una publică
    const isPublicPath = publicPaths.some(path =>
        location.pathname.startsWith(path)
    );

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <p>Loading...</p>
            </div>
        );
    }

    // Redirect dacă userul nu e logat și ruta nu este publică
    if (!user && !isPublicPath) {
        return <Navigate to="/" replace/>;
    }

    // Condiții de afișare sidebar
    const showSidebar =
        location.pathname.startsWith('/profile') ||
        location.pathname.startsWith('/home') ||
        location.pathname.startsWith('/friends') ||
        location.pathname.startsWith('/search') ||
        location.pathname.startsWith('/searchh') ||
        location.pathname.startsWith('/searcha') ||
        location.pathname.startsWith('/user/') ||
        location.pathname.startsWith('/chat/') ||
        location.pathname.startsWith('/forgot-password') ||
        location.pathname.startsWith('/reset-password/') ||
        location.pathname.startsWith('/admin');

    return (
        <div className="min-h-screen bg-gray-100">
            <Header/>
            <div className="flex-maindiv">
                {showSidebar && <SidebarMenu isAdmin={isAdmin}/>}
                <div className={`${!showSidebar ? 'full-width' : 'small-width'}`}>
                    <main className="p-4">
                        <Routes>
                            <Route path="/" element={<LandingPage/>}/>
                            <Route path="/login" element={<LoginPage/>}/>
                            <Route path="/signup" element={<SignUpPage/>}/>
                            <Route path="/home" element={<HomePage/>}/>
                            <Route path="/profile" element={<ProfilePage/>}/>
                            <Route path="/profile/:userId" element={<ProfilePage/>}/>
                            <Route path="/search" element={<SearchPage/>}/>
                            <Route path="/searchh" element={<SearchChat/>}/>
                            <Route path="/chat/:userId" element={<ChatPage/>}/>
                            <Route path="/user/:userId" element={<UserProfilePage/>}/>
                            <Route path="/friends" element={<FriendsPage/>}/>
                            <Route path="/forgot-password" element={<ForgotPasswordPage/>}/>
                            <Route path="/searcha" element={<SearchPageAdmin/>}/>
                            <Route path="/reset-password/:userId" element={<ResetPassword/>}/>

                            {isAdmin && (
                                <Route path="/admin/manage" element={<AccountsManagementPage/>}/>
                            )}
                        </Routes>
                    </main>
                </div>
            </div>
        </div>
    );
}

export default App;