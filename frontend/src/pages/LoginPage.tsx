import {Helmet} from "react-helmet";
import { LoginForm } from "../components/LoginForm";
import { LoginResponse } from "../model/LoginResponse";

export default function LoginPage() {


    const onLoginSuccess = (res: LoginResponse) => {
        window.open(res.usosURL);
    }


    return <>
        <Helmet>
            <title>Zapisy na projekty ∙ Zaloguj się</title>
        </Helmet>
        <div className="App container-fluid projects-helper-login-page">
            <div className="App projects-helper-login-page-cont">
                <LoginForm onSuccess={onLoginSuccess} onError={() => alert("error")}/>
            </div>
        </div>
    </>
}