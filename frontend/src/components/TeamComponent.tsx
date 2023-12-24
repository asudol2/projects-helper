import { useNavigate } from "react-router-dom";
import { TeamRequestResponse } from "../model/TeamRequstResponse";

interface TeamComponentProps {
    key: number;
    index: number;
    teamRequest: TeamRequestResponse;
    displayCount: boolean;
}

export function TeamComponent(props: TeamComponentProps) {

    return (
        <div className="projects-helper-team">
            <div>
                {
                    props.displayCount && 
                    <div className="projects-helper-team-index-label"> Zespół {props.index + 1}:</div>
                }
            </div>
            {
                <div className="projects-helper-team-participants-container">
                    {props.teamRequest.participants.map((participant, participantIndex) => (
                        <div className="projects-helper-team-participant-row" key={participantIndex}>
                            {participant.firstName} {participant.lastName}
                        </div>
                    ))}
                </div>
            }
        </div>
    )
}
