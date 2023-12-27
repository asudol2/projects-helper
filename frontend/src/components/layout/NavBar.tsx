import { Link, useNavigate, } from "react-router-dom";
import { SecurityHelper } from "../../helpers/SecurityHelper";
import { useEffect, useState } from "react";
import { Requests } from "../../requests/Requests";
import { useUsosTokens } from "../../contexts/UsosTokensContext";
import "../../style/layout.css"

const NavBar = () => {
    const navigate = useNavigate();

    const [userName, setUsername] = useState("");
    const {token, setToken, secret, setSecret} = useUsosTokens();

    useEffect(() => {
        if (!token || !secret) {
            Requests.getOAuthCredentials().then(res => res.res).then(data => {
                if (data !== undefined) {
                    setToken(data?.token);
                    setSecret(data?.secret);
                } else {
                    navigate("/login");
                }
            })
            .catch(error => {
                navigate("/login");
            });
        } else {
            Requests.getUserData(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    setUsername(data?.firstName + " " + data?.lastName);
                    SecurityHelper.saveUserId(data.ID);
                } else {
                    navigate("/login");
                }
            })
            .catch(error => {
                SecurityHelper.clearStorage();
                navigate("/login");
            });
        }
    }, [token, setToken, secret, setSecret]);


    return <div>
        <nav className="navbar navbar-dark projects-helper-navbar">
            <ul className="navbar-nav projects-helper-navbar-nav">
                <li className="nav-item projects-helper-nav-item">
                    <Link className="nav-link projects-helper-navbar-link-main" to="/">Zapisy na projekty</Link>
                </li>
            </ul>
            <ul className="navbar-nav projects-helper-navbar-nav ms-auto">

                {SecurityHelper.isUserLoggedIn() && <div>
                    <li className="nav-item projects-helper-nav-item-username">
                        <Link className="nav-link projects-helper-navbar-link" to="/profile">
                            <span>{userName}</span>
                        </Link>
                    </li>
                    <li className="nav-item projects-helper-nav-item">
                    <Link onClick={() => {
                        setToken(null);
                        setSecret(null);
                        SecurityHelper.clearStorage();
                        navigate("/login");
                    }} className="nav-link projects-helper-navbar-link" to="/login">
                        <i className="bi bi-box-arrow-right" /> Wyloguj się
                    </Link>
                </li>
                </div>}

            </ul>
        </nav>
    </div>
}

export default NavBar
