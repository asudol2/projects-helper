import {useState, useEffect} from 'react';
import {Requests} from "../requests/Requests";
import { UserDataResponse } from "../model/UserDataResponse";
import '../style/login.css';
import { SecurityHelper } from '../helpers/SecurityHelper';

interface UserDataComponentProps {
    onSuccess: (response: UserDataResponse) => void,
    onError: () => void
}

export function UserDataComponent(props: UserDataComponentProps) {
    const [userName, setUsername] = useState("");

    useEffect(() => {
        let loginToken = SecurityHelper.getLoginToken();
        Requests.getOAuthCredentials().then(res => res.res).then(data => {
            if (data !== undefined) {
                Requests.getUserData(data?.token, data?.secret).then(res => res.res).then(data => {
                    if (data !== undefined)
                        setUsername(data?.firstName+"-"+data?.lastName+"-"+data?.id)
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

    return (
        <div className="container-fluid projects-helper-user-data-cont">
            <div className="row projects-helper-user-data-row">
                <div className="col-lg-3 col-md-6 col-sm-12">
                    Nazywasz się: {userName}
                </div>
            </div>
        </div>)
}
