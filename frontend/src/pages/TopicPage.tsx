import { Helmet } from "react-helmet";
import { Requests } from "../requests/Requests";
import { useEffect, useState } from "react";
import { Topic } from "../model/Topic";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { useNavigate } from "react-router-dom";
import { useParams } from 'react-router-dom';
import { CreateTeamComponent } from "../components/CreateTeamComponent";
import { TopicTeamsComponent } from "../components/TopicTeamsComponent";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { LoadingComponent } from "../components/LoadingComponent";
import Content from "../components/layout/Content";
import "../style/shared.css"
import "../style/topics.css";


export default function TopicPage() {
    const { topicId } = useParams();
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [topic, setTopic] = useState<Topic | null>(null);
    const [creatingTeam, setCreatingTeam] = useState<boolean>(false);
    const [loadingTeamRequests, setLoadingTeamRequests] = useState<boolean>(false);
    const [loadingTeams, setLoadingTeams] = useState<boolean>(false);
    const [teamRequests, setTeamRequests] = useState<TeamRequestResponse[]>([]);
    const [teams, setTeams] = useState<TeamRequestResponse[]>([]);
    const navigate = useNavigate();


    useEffect(() => {
        if (token && secret) {
            if (topicId) {
                Requests.getTopicById(token, secret, topicId).then(res => res.res).then(data => {
                    if (data !== undefined) {
                        setTopic(data);
                        if (!data.temporary) {
                            getTopicTeamRequests(token, secret);
                            getTopicTeams(token, secret);
                        }
                    }
                })
                .catch(error => {
                    SecurityHelper.clearStorage();
                    navigate("/login");
                });
            }
        }

    }, [token, setToken, secret, setSecret]);

    const createTeam = () => {
        setCreatingTeam(true)
    }

    const removeTopicRequest = () => {
        if (!token || !secret || !topic)
            return;
        if (!window.confirm("Czy na pewno chcesz wycofać propozycję tematu?")) {
            return;
        }
        Requests.removeTopicRequest(token, secret, topic?.courseID, topic?.title).then(res => res.res).then(data => {
            if (data !== undefined && data) {
                navigate(-1);
            } else {
                console.log("error");
            }
        })
        .catch(error => {
            navigate("/login");
            SecurityHelper.clearStorage();
        })
    }

    const loadTopicTeamsOrTeamRequests = (
        token: string,
        secret: string,
        loadRequests: boolean,
        loadingCallback: (arg: boolean) => void, 
        resultCallback: (arg: TeamRequestResponse[]) => void
    ) => {
        loadingCallback(true);
        Requests.getUserTeamsOrTeamRequests(token, secret, loadRequests).then(res => res.res).then(data => {
            if (data !== undefined) {
                const result = data.filter(item => String(item.topicId) == topicId);
                resultCallback(result);
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

    const getTopicTeamRequests = (token: string, secret: string) => {
        loadTopicTeamsOrTeamRequests(token, secret, true, setLoadingTeamRequests, setTeamRequests);
    }

    const getTopicTeams = (token: string, secret: string) => {
        loadTopicTeamsOrTeamRequests(token, secret, false, setLoadingTeams, setTeams);
    }

    const teamCreated = () => {
        setCreatingTeam(false);
        if (token && secret && !topic?.temporary) {
            setTeamRequests([]);
            getTopicTeamRequests(token, secret);
            setTeams([]);
            getTopicTeams(token, secret);
        }
    }


    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ Szczegóły projektu</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-topic-details">
                    <div className="projects-helper-topic-label">Tytuł tematu:</div>
                    <div className="projects-helper-topic-title">{topic?.title}</div>
                    <div className="projects-helper-topic-label">Opis:</div>
                    <div className="projects-helper-topic-description">{topic && topic.description}</div>
                    <div className="projects-helper-topic-capacity">
                        <span className="projects-helper-topic-label">Minimalna liczebność zespołu: </span>
                        {topic?.minTeamCap}
                    </div>
                    <div className="projects-helper-topic-capacity">
                        <span className="projects-helper-topic-label">Maksymalna liczebność zespołu: </span>
                        {topic?.maxTeamCap}
                    </div>
                    {creatingTeam &&
                        <CreateTeamComponent courseId={String(topic?.courseID)} title={String(topic?.title)}
                            callback={teamCreated} />
                    }
                    {   loadingTeams &&
                        <LoadingComponent text="Ładowanie zespołów"/>
                    }
                    {!creatingTeam && teams.length > 0 &&
                            <TopicTeamsComponent
                                key={0}
                                teamRequests={teams}
                                title={"Zespoły, których członkiem jesteś:"}
                                confirmed={true}
                            />
                    }
                    {   loadingTeamRequests &&
                        <LoadingComponent text="Ładowanie propozycji zespołów"/>
                    }
                    {!creatingTeam && teamRequests.length > 0 &&
                            <TopicTeamsComponent
                                key={1}
                                teamRequests={teamRequests}
                                title={"Propozycje zespołów z Tobą w składzie:"}
                            />
                    }
                    {!creatingTeam &&
                        <button
                            className={`btn btn-primary projects-helper-choose-topic ${topic?.temporary ? 'disabled' : ''}`}
                            onClick={createTeam}
                        >
                            Zaproponuj zespół
                        </button>
                    }
                    {
                        topic?.temporary &&
                        <button className={"btn btn-primary projects-helper-cancel-topic"}
                            onClick={removeTopicRequest}
                        >
                            Wycofaj propozycję tematu
                        </button>
                    }
                </div>
            </Content>
        </>
    );
}
