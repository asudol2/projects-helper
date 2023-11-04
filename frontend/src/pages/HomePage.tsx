import { Helmet } from "react-helmet";
import { useParams } from 'react-router-dom';
import { UserDataComponent } from "../components/UserDataComponent";
import { UserDataResponse } from "../model/UserDataResponse";

export default function LoginPage() {
    const { usosAccessToken, usosAccessSecret } = useParams();


    const onGetUserDataSuccess = (res: UserDataResponse) => {
     console.log(res.firstName)   
    }

    return <>
        <Helmet>
            <title>Zapisy na projekty ∙ Strona główna</title>
        </Helmet>
        <div className="App container-fluid projects-helper-main-page">
            {usosAccessToken && usosAccessSecret ? (
                <UserDataComponent token={usosAccessToken} secret={usosAccessSecret} onSuccess={onGetUserDataSuccess} onError={() => alert("error")}/>
                ) : <div>Błąd autoryzacji</div>
            }
            
        </div>
    </>
}