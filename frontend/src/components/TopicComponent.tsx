import { useNavigate } from "react-router-dom";
import { Topic } from "../model/Topic";

interface TopicComponentProps {
    topic: Topic;
    key: number;
    index: number;
}

export function TopicComponent(props: TopicComponentProps) {
    const navigate = useNavigate();
    const { id, courseID, title, temporary } = props.topic;


    const handleClick = () => {
        navigate("/topic/"+id);
    }
    return (
        <div className="container-fluid projects-helper-item-row" onClick={handleClick}>
            {props.index}. {title} {temporary === true && (<span className="projects-helper-temporary-topic">niezatwierdzony</span>)}
        </div>
    )
}
