import { Helmet } from "react-helmet";
import { useParams } from 'react-router-dom';
import { UserDataComponent } from "../components/UserDataComponent";
import { UserDataResponse } from "../model/UserDataResponse";
import Content from "../components/layout/Content";

export default function LoginPage() {
    const onGetUserDataSuccess = (res: UserDataResponse) => {
    
    }

    return <>
        <Helmet>
            <title>Zapisy na projekty ∙ Strona główna</title>
        </Helmet>
        <Content>
            <div className="App container-fluid projects-helper-main-page">
                {
                    <UserDataComponent onSuccess={onGetUserDataSuccess} onError={() => alert("error")} />
                }

            </div>
        </Content>

    </>
}