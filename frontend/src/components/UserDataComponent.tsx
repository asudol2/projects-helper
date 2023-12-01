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

    return (
        <div className="container-fluid projects-helper-user-data-cont">
            <div className="row projects-helper-user-data-row">

            </div>
        </div>)
}
