import { Helmet } from "react-helmet";
import { Requests } from "../requests/Requests";
import { useEffect, useState } from "react";
import { Topic } from "../model/Topic";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { useNavigate } from "react-router-dom";
import { useParams } from 'react-router-dom';
import { CreateTeamComponent } from "../components/CreateTeamComponent";
import Content from "../components/layout/Content";
import "../style/shared.css"
import "../style/topics.css";


export default function TopicPage() {
    const { topicId } = useParams();
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [topic, setTopic] = useState<Topic | null>(null);
    const [creatingTeam, setCreatingTeam] = useState<boolean>(false);
    const navigate = useNavigate();


    useEffect(() => {
        if (token && secret) {
            if (topicId) {
                Requests.getTopicById(token, secret, topicId).then(res => res.res).then(data => {
                    if (data !== undefined) {
                        setTopic(data);
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

    const teamCreated = () => {
        setCreatingTeam(false);
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
                        <span className="projects-helper-topic-label">Minimalny skład zespołu: </span>
                        {topic?.minTeamCap}
                    </div>
                    <div className="projects-helper-topic-capacity">
                        <span className="projects-helper-topic-label">Maksymalny skład zespołu: </span>
                        {topic?.maxTeamCap}
                    </div>
                    {creatingTeam &&
                        <CreateTeamComponent courseId={String(topic?.courseID)} title={String(topic?.title)} 
                            callback={teamCreated}/>}
                    { !creatingTeam &&
                        <button
                            className={`btn btn-primary projects-helper-choose-topic ${topic?.temporary ? 'disabled': ''}`}
                            onClick={createTeam}
                        >
                            Stwórz zespół do realizacji tego tematu
                        </button>
                    }
                    {
                        topic?.temporary && <button className={"btn btn-primary projects-helper-cancel-topic"}>
                            Wycofaj propozycję tematu
                        </button>
                    }
                </div>
            </Content>
        </>
    );
}