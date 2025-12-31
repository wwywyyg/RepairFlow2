import { createContext,useContext,useEffect,useState } from "react";
import{ getCurrentUser } from '../Api/Services/AuthServices';

const AuthContext = createContext(null);

export const AuthProvider = ({children}) => {
    const [user,setUser] = useState(null);
    const [token,setToken] = useState(null);
    const [loading,setLoading] = useState(true);

    // initialize read local storge data
    useEffect(() => {
        // save local data to instance
        const savedUser = localStorage.getItem('user');
        const savedToken = localStorage.getItem('token');
        
        // check login status , if not login , set loading false and return, otherwise set token
        if(!savedToken){
            setLoading(false);
            return;
        }
        setToken(savedToken);

        //check user info
        if(savedUser){
            try{
                const parsedUser = JSON.parse(savedUser);
                setUser(parsedUser);
            }catch{
                console.warn("failed to parse saver user from local storage");
            }
        }

        // use token to call api /auth/cuer/me to sync data
        const syncUser = async () =>{
            try{
                const res = await getCurrentUser();
                const latestUser = res.data?? res;

                if(latestUser){
                    setUser(latestUser);
                    localStorage.setItem('user',JSON.stringify(latestUser));
                }
            }catch(error){
                console.error("Failed to fetch current user",error);
            } finally {
                setLoading(false);
            }
        }
        syncUser();

    },[]);

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


