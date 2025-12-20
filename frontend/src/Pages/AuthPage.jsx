import {Container,Row,Col} from 'react-bootstrap';
import AuthForm from '../Components/AuthForm';

const AuthPage = () =>{

    return(
    <div className="bg-light min-vh-100 d-flex align-items-center justify-content-center">
        <Container>
            <Row className="justify-content-center">
                <Col md={6} lg={4} className="d-flex justify-content-center">
                <AuthForm/>
                </Col>
            </Row>
        </Container>
    </div>
    )

}

export default AuthPage;