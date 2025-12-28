import { createContext,useContext,useEffect,useState } from "react";

const AuthContext = createContext(null);

export const AuthProvider = ({children}) => {
    const [user,setUser] = useState(null);
    const [token,setToken] = useState(null);
    const [loading,setLoading] = useState(true);

    // initialize read local storge data
    useEffect(() => {
        const savedUser = localStorage.getItem('user');
        const savedToken = localStorage.getItem('token');
        if(savedUser && savedToken){
            setToken(savedToken);
            try{
                setUser(JSON.parse(savedUser));
            } catch {
                setUser(null)
            }
        }
        setLoading(false);
    },[])

    const login = (userData,jwtToken) =>{
        setUser(userData);
        setToken(jwtToken);
        localStorage.setItem('user',JSON.stringify(userData));
        localStorage.setItem('token',jwtToken);
    }

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    }

    return (
        <AuthContext.Provider value={{user,token,login,logout,loading}}>
            {children}
        </AuthContext.Provider>
    )
}


export const useAuth = () => useContext(AuthContext);