import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {UserProvider} from './context/UserContext.tsx'
import {BrowserRouter} from 'react-router-dom'

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
        <UserProvider>
            <App/>
        </UserProvider>,
    </BrowserRouter>
)
