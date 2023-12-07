import { Helmet } from "react-helmet";
import Content from "../components/layout/Content";
import { Requests } from "../requests/Requests";
import { useEffect, useState } from "react";
import { Topic } from "../model/Topic";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { useNavigate } from "react-router-dom";
import { useParams } from 'react-router-dom';
import { TopicComponent } from "../components/TopicComponent";
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

    }, [token, setToken, secret, setSecret]);


    return (
        <>
            <Helmet>
                <title>Zapisy na projekty ∙ Szczegóły projektu</title>
            </Helmet>
            <Content>
                <div className="App container-fluid projects-helper-topic-details">
                    <div className="projects-helper-topic-label">Tytuł:</div>
                    <div className="projects-helper-page-header">{topic && topic.title}</div>
                    <div className="projects-helper-topic-label">Opis:</div>
                    <div className="projects-helper-topic-description">{topic && topic.description}</div>
                </div>
            </Content>
        </>
    );
}