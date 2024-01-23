import { Helmet } from "react-helmet";
import { Requests } from "../requests/Requests";
import { useEffect, useState } from "react";
import { Topic } from "../model/Topic";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { useNavigate } from "react-router-dom";
import { useParams } from 'react-router-dom';
import { StudentViewTopicComponent } from "../components/StudentViewTopicComponent";
import { StaffViewTopicComponent } from "../components/StaffViewTopicComponent";
import Content from "../components/layout/Content";
import "../style/shared.css"
import "../style/topics.css";


export default function TopicPage() {
    const { topicId } = useParams();
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [topic, setTopic] = useState<Topic | null>(null);
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

    }, [token, setToken, secret, setSecret, navigate, topicId]);

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
                    {
                        token && secret && topicId && topic && SecurityHelper.getUsetType() === "STUDENT" &&
                        <StudentViewTopicComponent
                            token={token}
                            secret={secret}
                            topicId={topicId}
                            topic={topic}
                        />
                    }
                    {
                        token && secret && topicId && topic && SecurityHelper.getUsetType() === "STAFF" &&
                        <StaffViewTopicComponent
                            token={token}
                            secret={secret}
                            topicId={topicId}
                            topic={topic}
                        />
                    }
                </div>
            </Content>
        </>
    );
}
