import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CreateTeamComponent } from "./CreateTeamComponent";
import { LoadingComponent } from "./LoadingComponent";
import { TopicTeamsComponent } from "./TopicTeamsComponent";
import { Topic } from "../model/Topic";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { Requests } from "../requests/Requests";

interface StudentViewTopicComponentProps {
    token: string;
    secret: string;
    topicId: string;
    topic: Topic;
}

export function StudentViewTopicComponent(props: StudentViewTopicComponentProps) {
    const [creatingTeam, setCreatingTeam] = useState<boolean>(false);
    const [loadingTeamRequests, setLoadingTeamRequests] = useState<boolean>(false);
    const [loadingTeams, setLoadingTeams] = useState<boolean>(false);
    const [teamRequests, setTeamRequests] = useState<TeamRequestResponse[]>([]);
    const [teams, setTeams] = useState<TeamRequestResponse[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        if (!props.topic.temporary) {
            getTopicTeamRequests(props.token, props.secret);
            getTopicTeams(props.token, props.secret);
        }
    }, []);

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
                const result = data.filter(item => String(item.topicId) == props.topicId);
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

    const createTeam = () => {
        setCreatingTeam(true)
    }
    const teamCreated = () => {
        setCreatingTeam(false);
        if (props.token && props.secret && !props.topic?.temporary) {
            setTeamRequests([]);
            getTopicTeamRequests(props.token, props.secret);
            setTeams([]);
            getTopicTeams(props.token, props.secret);
        }
    }

    const removeTopicRequest = () => {
        if (!props.token || !props.secret || !props.topic)
            return;
        if (!window.confirm("Czy na pewno chcesz wycofać propozycję tematu?")) {
            return;
        }
        Requests.removeTopicRequest(props.token, props.secret, props.topic?.courseID, props.topic?.title).then(res => res.res).then(data => {
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


    return (
        <div className="container-fluid projects-helper-item-topic-student">
            {creatingTeam &&
                <CreateTeamComponent courseId={String(props.topic?.courseID)} title={String(props.topic?.title)}
                    callback={teamCreated} />
            }
            {loadingTeams &&
                <LoadingComponent text="Ładowanie zespołów" />
            }
            {!creatingTeam && teams.length > 0 &&
                <TopicTeamsComponent
                    key={0}
                    teamRequests={teams}
                    title={"Zespoły, których członkiem jesteś:"}
                    confirmed={true}
                />
            }
            {loadingTeamRequests &&
                <LoadingComponent text="Ładowanie propozycji zespołów" />
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
                    className={`btn btn-primary projects-helper-choose-topic ${props.topic?.temporary ? 'disabled' : ''}`}
                    onClick={createTeam}
                >
                    Zaproponuj zespół
                </button>
            }
            {
                props.topic?.temporary &&
                <button className={"btn btn-primary projects-helper-cancel-topic"}
                    onClick={removeTopicRequest}
                >
                    Wycofaj propozycję tematu
                </button>
            }
        </div>
    )
}
