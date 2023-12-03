import { Helmet } from "react-helmet";
import { LoginForm } from "../components/LoginForm";
import { LoginResponse } from "../model/LoginResponse";
import { SecurityHelper } from "../helpers/SecurityHelper";


export default function LoginPage() {

    const onLoginSuccess = (res: LoginResponse) => {
        SecurityHelper.saveLoginToken(res.loginToken);
        window.location.href = res.usosURL;
    }


    return <>
        <Helmet>
            <title>Zapisy na projekty ∙ Zaloguj się</title>
        </Helmet>
        <div className="App container-fluid projects-helper-login-page">
            <div className=""><h1>ZAPISY NA PROJEKTY</h1></div>
            <div className="App projects-helper-login-page-cont">
                <LoginForm onSuccess={onLoginSuccess} onError={() => alert("error")}/>
            </div>
        </div>
    </>
}