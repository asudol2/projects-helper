import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { LoadingComponent } from "./LoadingComponent";
import { Topic } from "../model/Topic";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { Requests } from "../requests/Requests";
import { TeamComponent } from "./TeamComponent";

interface StaffViewTopicComponentProps {
    token: string;
    secret: string;
    topicId: string;
    topic: Topic;
}

export function StaffViewTopicComponent(props: StaffViewTopicComponentProps) {
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
        Requests.getCourseTeamsOrTeamRequests(token, secret, props.topic.courseID, loadRequests)
        .then(res => res.res).then(data => {
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


    const removeTopicRequest = () => {
        if (!props.token || !props.secret || !props.topic)
            return;
        if (!window.confirm("Czy na pewno chcesz odrzucić propozycję tematu?")) {
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

    const handleRemove = (index: number) => {
        const newRequests = [...teamRequests];
        newRequests.splice(index, 1);
        setTeamRequests(newRequests);
        getTopicTeams(props.token, props.secret);
    };


    return (
        <div className="container-fluid projects-helper-item-topic-student">
            {loadingTeams &&
                <LoadingComponent text="Ładowanie zespołów" />
            }
            <div>Przydzielone zespoły:</div>
            {teams.length > 0 &&
                <div>
                    {teams.map((team, index) => (
                        <TeamComponent
                            key={index}
                            index={index}
                            teamRequest={team}
                            displayCount={true}
                            onDestroy={() => { handleRemove(index) }}
                            confirmed={true}
                            staffView={true}
                        />
                    ))}
                </div>
            }
            {
                !loadingTeams && teams.length == 0 &&
                <p className="projects-helper-empty-container-message">Żaden zespół nie jest jeszcze przydzielony</p>
            }
            {loadingTeamRequests &&
                <LoadingComponent text="Ładowanie propozycji zespołów" />
            }
            <div>Niezatwierdzone zespoły:</div>
            {
                !loadingTeamRequests && teamRequests.length == 0 &&
                <p className="projects-helper-empty-container-message">Brak niezatwierdzonych zespołów</p>
            }
            {teamRequests.length > 0 &&
                <div>
                    {teamRequests.map((teamRequest, index) => (
                        <TeamComponent
                            key={index}
                            index={index}
                            teamRequest={teamRequest}
                            displayCount={true} 
                            onDestroy={() => { handleRemove(index) }}
                            staffView={true}
                        />
                    ))}
                </div>
            }
            {
                props.topic?.temporary &&
                <button className={"btn btn-primary projects-helper-cancel-topic"}
                    onClick={removeTopicRequest}
                >
                    Odrzuć propozycję tematu
                </button>
            }
        </div>
    )
}
