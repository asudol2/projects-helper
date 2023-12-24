import { useState } from "react";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { TeamComponent } from "./TeamComponent";
import "../style/teams.css";

interface TopicTeamsComponentProps {
    teamRequests: TeamRequestResponse[];
    key: number;
}

export function TopicTeamsComponent(props: TopicTeamsComponentProps) {
    const [isExtended, setIsExtended] = useState<boolean>(false);

    const handleClick = () => {
        setIsExtended((prevIsExtended) => !prevIsExtended);
    };

    return (
        <div
            className="container-fluid projects-helper-topic-teams"
            onClick={handleClick}
        >
            <div className="projects-helper-teams-topic-title">
                Tytuł: {props.teamRequests[0].topicTitle}, przedmiot: <span className="projects-helper-teams-course-name">{
                    props.teamRequests[0].courseName}:
                </span>
            </div>
            <div
                className={`projects-helper-topic-team-details ${isExtended ? 'extended' : 'compressed'}`}
            >
                {
                    props.teamRequests.map((teamRequest, index) => (
                        <TeamComponent index={index} key={index} teamRequest={teamRequest} displayCount={props.teamRequests.length > 1}/>
                    ))
                }
            </div>
        </div>
    )
}
