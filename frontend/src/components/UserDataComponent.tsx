import {useState, useEffect} from 'react';
import {Requests} from "../requests/Requests";
import { UserDataResponse } from "../model/UserDataResponse";
import '../style/login.css';
import { useUsosTokens } from '../contexts/UsosTokensContext';

interface UserDataComponentProps {
    onSuccess: (response: UserDataResponse) => void,
    onError: () => void
}

export function UserDataComponent(props: UserDataComponentProps) {
    const [courses, setCourses] = useState(null);
    const { token, setToken, secret, setSecret } = useUsosTokens();


    useEffect(() => {
        if (token && secret) {
            Requests.getAllCourses(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    console.log(data);
                    setCourses(data);
                }
            })
            .catch(error => {
                console.log(error);
            });
        }
    }, [token, setToken, secret, setSecret]);

    return (
        <div className="container-fluid projects-helper-user-data-cont">
            <div className="row projects-helper-user-data-row">

            </div>
        </div>)
}
