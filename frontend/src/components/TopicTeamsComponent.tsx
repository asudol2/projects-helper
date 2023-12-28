import { useEffect, useState } from "react";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { TeamComponent } from "./TeamComponent";
import "../style/teams.css";

interface TopicTeamsComponentProps {
    teamRequests: TeamRequestResponse[];
    title?: string | null;
    key: number;
    confirmed?: boolean | null;
}

export function TopicTeamsComponent(props: TopicTeamsComponentProps) {
    const [isExtended, setIsExtended] = useState<boolean>(false);
    const [teamRequests, setTeamRequests] = useState<TeamRequestResponse[]>([]);

    useEffect(() => {
        setTeamRequests(props.teamRequests);
    }, []);

    const handleClick = () => {
        setIsExtended((prevIsExtended) => !prevIsExtended);
    };

    const handleRemove = (index: number) => {
        const newRequests = [...props.teamRequests];
        newRequests.splice(index, 1);
        setTeamRequests(newRequests);
    };

    return (
        <div>
        { teamRequests.length > 0 &&
            <div
                className="container-fluid projects-helper-topic-teams"
                onClick={handleClick}
            >
                { props.title == null &&
                    <div className="projects-helper-teams-topic-title-container">
                        Przedmiot: <span className="projects-helper-teams-course-name">
                            {props.teamRequests[0].courseName}
                        </span>
                        <br/>Tytuł: <span className="projects-helper-teams-topic-title">{props.teamRequests[0].topicTitle}</span>
                        : <span>({teamRequests.length})</span>
                    </div>
                }
                { props.title != null &&
                        <div className="projects-helper-teams-topic-title-container">
                        {props.title} ({teamRequests.length})
                    </div>
                }
                <div
                    className={`projects-helper-topic-team-details ${isExtended ? 'extended' : 'compressed'}`}
                >
                    {
                        teamRequests.map((teamRequest, index) => (
                            <TeamComponent
                                index={index}
                                key={index}
                                teamRequest={teamRequest}
                                displayCount={props.teamRequests.length > 1}
                                confirmed={props.confirmed}
                                onDestroy={() => {handleRemove(index)}}
                            />
                        ))
                    }
                </div>
            </div>
        }
        </div>
    )
}
