import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import { Requests } from "../requests/Requests";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { LoadingComponent } from "../components/LoadingComponent";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { TopicTeamsComponent } from "../components/TopicTeamsComponent";
import Content from "../components/layout/Content";
import "../style/shared.css"


export default function UserPage() {
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [teamRequests, setTeamRequests] = useState<Map<number, TeamRequestResponse[]>>(new Map());
    const [teams, setTeams] = useState<Map<number, TeamRequestResponse[]>>(new Map()); //TODO weryfikacja tego
    const [loadingTeamRequests, setLoadingTeamRequests] = useState<boolean>(false);
    const [loadingTeams, setLoadingTeams] = useState<boolean>(false);
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

    const loadTeamOrTeamRequests = (
        token: string,
        secret: string,
        loadRequests: boolean,
        loadingCallback: (arg: boolean) => void, 
        resultCallback: (arg: Map<number, TeamRequestResponse[]>) => void
    ) => {
        loadingCallback(true);
        Requests.getUserTeamsOrTeamRequests(token, secret, loadRequests).then(res => res.res).then(data => {
            if (data !== undefined) {
                resultCallback(groupTeamsByTopicId(data));
            } else {
                SecurityHelper.clearStorage();
                navigate("/login");
            }
        })
        .catch(err => {
            SecurityHelper.clearStorage();
            navigate("/login");
        })
        .finally(() => {
            loadingCallback(false);
        });
    };

    const loadTeamRequests = (token: string, secret: string) => {
        loadTeamOrTeamRequests(token, secret, true, setLoadingTeamRequests, setTeamRequests);
    };

    const loadTeams = (token: string, secret: string) => {
        loadTeamOrTeamRequests(token, secret, false, setLoadingTeams, setTeams);
    };

    useEffect(() => {
        if (token && secret) {
            loadTeams(token, secret);
            loadTeamRequests(token, secret);
        }
    }, [token, setToken, secret, setSecret]);

    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ Profil użytkownika</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-user-page">
                    <p>Zespoły projektowe, których jesteś członkiem:</p>
                    {
                        loadingTeams &&
                        <LoadingComponent text="Ładowanie zespołów, których jesteś członkiem"/>
                    }
                    {
                        !loadingTeams && teams.size == 0 &&
                        <p className="projects-helper-empty-container-message">Nie jesteś członkiem żadnego zespołu.</p>
                    }
                    {
                        teams != null && Array.from(teams.entries()).map(([index, groupedTeamRequests])=> (
                            <TopicTeamsComponent key={index} teamRequests={groupedTeamRequests} confirmed={true}/>
                        ))
                    }
                    <p>Niepotwierdzone zespoły projektowe:</p>
                    {
                        loadingTeamRequests &&
                        <LoadingComponent text="Ładowanie niepotwierdzonych zespołów"/>
                    }
                    {
                        !loadingTeamRequests && teamRequests.size == 0 &&
                        <p className="projects-helper-empty-container-message">Nie jesteś członkiem żadnego niepotwierdzonego zespołu.</p>
                    }
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