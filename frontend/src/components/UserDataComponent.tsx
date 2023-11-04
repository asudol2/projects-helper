import {useState, useEffect} from 'react';
import {Requests} from "../requests/Requests";
import { UserDataResponse } from "../model/UserDataResponse";
import '../style/login.css';

interface UserDataComponentProps {
    onSuccess: (response: UserDataResponse) => void,
    onError: () => void
    token: string
    secret: string
}

export function UserDataComponent(props: UserDataComponentProps) {
    const [userName, setUsername] = useState("");

    useEffect(() => {
        console.log("useEffect()")
        Requests.getUserData(props.token, props.secret).then(res => res.res).then(data => {
            if (data !== undefined)
                setUsername(data?.firstName+" "+data?.lastName)
        })
        .catch(error => {
            console.log(error);
        });
      }, [props.token, props.secret]);

    return (
        <div className="container-fluid projects-helper-user-data-cont">
            <div className="row projects-helper-user-data-row">
                <div className="col-lg-3 col-md-6 col-sm-12">
                    Nazywasz się: {userName}
                </div>
            </div>
        </div>)
}
