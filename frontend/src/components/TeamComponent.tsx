import { useUsosTokens } from "../contexts/UsosTokensContext";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { Requests } from "../requests/Requests";
import "../style/teams.css"

interface TeamComponentProps {
    key: number;
    index: number;
    teamRequest: TeamRequestResponse;
    displayCount: boolean;
    confirmed?: boolean | null;
}

const leaveTeam = (teamRequestId: number, token: string | null, secret: string | null, event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (token && secret) {
        if (!window.confirm("Czy na pewno chcesz opuścić zespół?")) {
            return;
        }
        Requests.rejectTeamRequest(token, secret, teamRequestId).then(res => res.res).then(data => {
            if (data) {
                console.log("opuszczono zespół");  //TODO coś z tym trzeba zrobić
            }
        })
        .catch(err => {
            console.log(err)
        });
    }
}

export function TeamComponent(props: TeamComponentProps) {
    const { token, secret } = useUsosTokens();

    return (
        <div className="projects-helper-team">
            <div>
                {
                    props.displayCount && 
                    <div className="projects-helper-team-index-label"> Zespół {props.index + 1}:</div>
                }
            </div>
            <div className="projects-helper-team-container">
                {
                    <div className="projects-helper-team-participants-container">
                        {props.teamRequest.participants.map((participant, participantIndex) => (
                            <div className="projects-helper-team-participant-row" key={participantIndex}>
                                {participant.firstName} {participant.lastName}
                            </div>
                        ))}
                    </div>
                }
                { !props.confirmed &&
                    <button className="btn btn-primary projects-helper-leave-team"
                        onClick={(e) => leaveTeam(props.teamRequest.teamRequestId, token, secret, e)}
                    >
                        Opuść zespół
                    </button>
                }
            </div>
        </div>
    )
}
