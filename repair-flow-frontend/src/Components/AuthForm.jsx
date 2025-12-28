import { use, useState } from "react";
import {Form,Button,Card,Alert,Spinner} from 'react-bootstrap'
import {Link, useNavigate} from 'react-router-dom';
import { registerUser, loginUser } from "../Api/Services/AuthServices";
import {useAuth} from '../Context/AuthContext'

const AuthForm = () => {
    // define state
    const navigate = useNavigate();
    const {login} = useAuth();
    const [isLogin, setIsLogin] = useState(true);
    const [loading, setLoading] = useState(false); // loading state
    const [error, setError] = useState(""); // error info

    //  form data 
    const [formData , setFormData] = useState({
        firstName:'',
        lastName:'',
        email:'',
        password:'',
        confirmPassword:'',
        phone:''

    });

    // handle input change
    const handleChange =(e) =>{
        const{name,value} = e.target;
        setFormData(prev => ({...prev,[name]: value}));
    };

    // handle submit
    const handleSubmit =  async (e) =>{
        e.preventDefault();
        setLoading(true);
        setError("");
        
        if (!isLogin) {
            if (formData.password !== formData.confirmPassword) {
                setLoading(false);
                alert("Passwords do not match");
                return;
            }
        }


        try{
            if(isLogin){
                // login logic
                const response = await loginUser({
                    email: formData.email,
                    password: formData.password
                });
            
                const token = response.data?.token;
                const user = response.data?.user;
                
                if(token && user){
                    // use AuthContext save token and user to local storage
                    login(user,token);

                    // redirect to dashboard
                    navigate('/dashboard');
                }else{
                    setError("Login in Success but Toekn or User Info not found, Please check backend response");
                }
                // const token = response.data?.token || response.token;;

                // if(token){
                //     // save token
                //     localStorage.setItem('token',token);
               
                //     // save user info
                //     localStorage.setItem('user',JSON.stringify(response.data.user));
                
                //     // redirect to dashboard
                //     navigate('/dashboard');
                // }else{
                //     setError("Login in Success but Toekn not found, Please check backend");
                // }
            }else{
                // register logic

                const {confirmPassword, ...registerPlayload} = formData;
                const response = await registerUser(registerPlayload);
                alert("Register Success");
                setIsLogin(true);

                // const response = await registerUser(formData);
                // alert("Register Success");
                // setIsLogin(true);
            }
            
        }catch(err){
            console.error("Auth error",err);
            // try to read backend return error message
            const errorMessage = err.response?.data?.message || err.message;
            setError(errorMessage);
        }finally{
            setLoading(false);
        }

        // if(isLogin){
            
        //     const {email,password} = formData;
        //     const loginPayload = {email,password};
        //     console.log("execute login business",loginPayload)
        // }else{

        //     // const registerPlayload = {
        //     //     firstName: formData.firstName,
        //     //     lastName: formData.lastName,
        //     //     email: formData.email,
        //     //     password: formData.password,
        //     //     phone: formData.phone
        //     // }
        //     const {confirmPassword, ...registerPlayload} = formData;
        //     console.log("execute register business", registerPlayload)
        // }
    };


    // login or register mode switch 
    const toggleMode = () =>{
        setIsLogin(!isLogin);
        setError("");
        setFormData({firstName:'',lastName:'',email:'',password:'',confirmPassword:'',phone:''});
    }

    // passwords match
    const isPasswordMismatch = !isLogin && formData.confirmPassword   && formData.password !== formData.confirmPassword;

    return(
        <Card className="p-4 shadow" style={{width:'100%',maxWidth:'400px'}}>
            <Card.Body>
                <h2 className="text-center mb-4">
                    {isLogin? 'Login to your account' : 'Create a new account'}
                </h2>

                {error && <Alert variant="danger">{error}</Alert>}

                <Form onSubmit={handleSubmit}>
                    {/* in register mode  to show User first Name */}
                    {!isLogin &&(
                        <Form.Group className="mb-3" controlId="formRegisterFirstName">
                            <Form.Label>First Name</Form.Label>
                            <Form.Control type="text" placeholder="Please enter your first name" name="firstName" value={formData.firstName} onChange={handleChange} required />
                        </Form.Group>
                    )}

                    {/* Last name in register mode  */}
                    {!isLogin &&(
                        <Form.Group className="mb-3" controlId="formRegisterLastName">
                            <Form.Label>Last Name</Form.Label>
                            <Form.Control type="text" placeholder="Please enter your last name" name="lastName" value={formData.lastName} onChange={handleChange} required />
                        </Form.Group>
                    )}

                    {/* Email  */}
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label>Email address</Form.Label>
                        <Form.Control type="email" placeholder="Please enter your email" name="email" value={formData.email} onChange={handleChange} required/>
                    </Form.Group>

                    {/* Password */}
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label>Password</Form.Label>
                        <Form.Control type="password" placeholder= "Please enter your password" name="password" value={formData.password} onChange={handleChange} required/> 
                    </Form.Group>

                    {/* Confirm Password */}
                    {!isLogin &&(
                        <Form.Group className="mb-3" controlId="formBasicConfirmPassword">
                            <Form.Label>Confirm password</Form.Label>
                            <Form.Control type="password" placeholder="Please enter password again" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} required />
                            {!isLogin && formData.confirmPassword && formData.password !== formData.confirmPassword &&(
                                <div className="text-danger">Passwords do not match</div>
                            )}
                        </Form.Group>
                    )}

                    {/* phone */}
                    {!isLogin &&(
                        <Form.Group className="mb-3" controlId="formBasicPhone">           
                            <Form.Label>Phone</Form.Label>
                            <Form.Control type="tel" placeholder="Please enter your phone number" name="phone" value={formData.phone} onChange={handleChange} required />
                        </Form.Group>
                    )}
                    


                    {/* Submit buttom */}
                    <div className="d-grid gap-2">
                        <Button variant="primary" type="submit" size="lg" disabled = {isPasswordMismatch}>
                            {isLogin? 'Login' : 'Register'}
                        </Button>
                    </div>
                </Form>

                <div className="text-center mt-3">
                    <small className="text-muted">
                        {isLogin? 'No account?' : 'Have account?'}
                        <span onClick={toggleMode} style={{cursor: 'pointer' , color: '#0d6efd', fontWeight:'bold' , marginLeft:'5px'}}>
                            {isLogin? 'Register':'Login'}
                        </span>
                    </small>
                </div>

                <div className="text-center mt-4">
                    <Link to="/" className="text-decoration-none text-secondary d-flex align-items-center justify-content-center gap-2" >
                    <span>&larr;</span>Back to Home
                    </Link>
                </div>
            </Card.Body>
        </Card>
    )



}

export default AuthForm;