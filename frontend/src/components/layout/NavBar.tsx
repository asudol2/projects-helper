import { Link, useNavigate, } from "react-router-dom";
import "../../style/layout.css"
import { SecurityHelper } from "../../helpers/SecurityHelper";
import { useEffect, useState } from "react";
import { Requests } from "../../requests/Requests";

const NavBar = () => {
    const navigate = useNavigate();

    const [userName, setUsername] = useState("");

    useEffect(() => {
        Requests.getOAuthCredentials().then(res => res.res).then(data => {
            if (data !== undefined) {
                Requests.getUserData(data?.token, data?.secret).then(res => res.res).then(data => {
                    if (data !== undefined)
                        setUsername(data?.firstName + " " + data?.lastName)
                })
                    .catch(error => {
                        console.log(error);
                    });
            }
        })
            .catch(error => {
                console.log(error);
            });
    }, []);

    return <div>
        <nav className="navbar navbar-dark projects-helper-navbar">
            <ul className="navbar-nav projects-helper-navbar-nav">
                <li className="nav-item projects-helper-nav-item">
                    <Link className="nav-link projects-helper-navbar-link-main" to="/">Zapisy na projekty</Link>
                </li>
            </ul>
            <ul className="navbar-nav projects-helper-navbar-nav ms-auto">

                {SecurityHelper.isUserLoggedIn() && <div>
                    <li className="nav-item projects-helper-nav-item-username"><span>{userName}</span></li>
                    <li className="nav-item projects-helper-nav-item">
                    <Link onClick={() => {
                        SecurityHelper.clearStorage();
                        navigate("/login")
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
