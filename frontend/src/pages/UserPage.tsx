import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import { Requests } from "../requests/Requests";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { CourseParticipant } from "../model/CourseParticipant";
import { TopicTeamsComponent } from "../components/TopicTeamsComponent";
import Content from "../components/layout/Content";
import "../style/shared.css"
import { TeamRequestResponse } from "../model/TeamRequstResponse";


export default function UserPage() {
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [teamRequests, setTeamRequests] = useState<Map<number, TeamRequestResponse[]>>(new Map());
    const navigate = useNavigate();

    const groupTeamsByTopicId = (teams: TeamRequestResponse[]) => {
        let result: Map<number, TeamRequestResponse[]> = new Map();
        for (const team of teams) {
            let existingList = result.get(team.topicId) || [];
            existingList.push(team);
            result.set(team.topicId, existingList);
        }
        return result;
    };

    useEffect(() => {
        if (token && secret) {
            Requests.getUserTeamRequests(token, secret).then(res => res.res).then(data => {
                if (data !== undefined) {
                    setTeamRequests(groupTeamsByTopicId(data));
                } else {
                    SecurityHelper.clearStorage();
                    navigate("/login");
                }
            })
            .catch(err => {
                SecurityHelper.clearStorage();
                navigate("/login");
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
                        teamRequests != null && Array.from(teamRequests.entries()).map(([index, groupedTeamRequests])=> (
                            <TopicTeamsComponent key={index} teamRequests={groupedTeamRequests} />
                        ))
                    }
                </div>
            </Content>
        </>
    );
}