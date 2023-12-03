import {FormEvent} from 'react';
import {Requests} from "../requests/Requests";
import { LoginResponse } from "../model/LoginResponse";
import '../style/login.css';

interface LoginFormProps {
    onSuccess: (response: LoginResponse) => void,
    onError: () => void
}

export function LoginForm(props: LoginFormProps) {
    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        Requests.login().then(res => {
            if (res?.res?.usosURL != "") {
                if (res.res !== undefined)
                    props.onSuccess(res.res);
            }
            else {
                props.onError()
            }
        });
    }

    return (
        <div className="container-fluid projects-helper-login-cont">
            <div className="row projects-helper-login-row">
                <div className="col-lg-3 col-md-6 col-sm-12">
                    <form onSubmit={handleSubmit}>
                        <div className="card text-black bg-light projects-helper-login-card">
                            <div className="projects-helper-login-submit">
                                <button type="submit" onClick={handleSubmit} className="btn btn-outline-success">Zaloguj się</button>
                            </div>
                            <p>Logowanie odbywa się poprzez system USOS</p>
                        </div>
                    </form>
                </div>
            </div>
        </div>)
}
