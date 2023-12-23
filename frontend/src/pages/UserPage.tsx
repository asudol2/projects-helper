import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import { Requests } from "../requests/Requests";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { CourseParticipant } from "../model/CourseParticipant";
import Content from "../components/layout/Content";
import "../style/shared.css"


export default function UserPage() {
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [teamRequests, setTeamRequests] = useState<Map<string, CourseParticipant[][]>>(new Map());
    const navigate = useNavigate();

    useEffect(() => {
        if (token && secret) {
            Requests.getUserTeamRequests(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    console.log(data);
                    setTeamRequests(data);
                } else {
                    console.log("CHUUUJ");
                }
            })
            .catch(err => {
                console.log(err);
            });
        }
    }, [token, setToken, secret, setSecret]);



    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ Profil użytkownika</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-user-page">
                    <p>Zespoły projektowe w których uczestniczysz:</p>
                    {
                        
                    }
                    <p>Niepotwierdzone zespoły projektowe:</p>
                    {

                    }
                </div>
            </Content>
        </>
    );
}