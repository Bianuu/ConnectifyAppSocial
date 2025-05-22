import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';

// Define the shape of the user data
type User = {
    id: string;
    email: string;
    username: string;
    role: string | undefined;
    bio: string;
    image: any;
    dateOfBirth: string;
    createdAt: string;
    updatedAt: string;
};


interface UserContextType {
    user: User | null;
    setUser: React.Dispatch<React.SetStateAction<User | null>>;
    loading: boolean;

}

const UserContext = createContext<UserContextType | undefined>(undefined);

// NOTE: Keeping useUserContext in the same file as UserProvider for project structure.
// This may disable Fast Refresh for this file, but it's safe to ignore.
// Fast Refresh only affects development hot reload behavior.
export const useUserContext = () => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error('useUserContext must be used within a UserProvider');
    }
    return context;
};


interface UserProviderProps {
    children: ReactNode;
}

export const UserProvider: React.FC<UserProviderProps> = ({children}) => {
    const [user, setUser] = useState<User | null>(null);

    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);


    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
    }, []);

    useEffect(() => {
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
        } else {
            localStorage.removeItem('user');
        }
    }, [user]);

    return (
        <UserContext.Provider value={{user, setUser, loading}}>
            {children}
        </UserContext.Provider>
    );
};