import { CourseParticipant } from "../model/CourseParticipant";

interface SearchResultProps {
    participant: CourseParticipant;
    rowId: number;
    onClick: (participant: CourseParticipant, rowId: number) => void;
}

export function SearchResultComponent(props: SearchResultProps) {

    const onClick = () => {
        props.onClick(props.participant, props.rowId)
    }

    return (
        <div className="projects-helper-search-result-item" onClick={onClick}>
                {props.participant.firstName} {props.participant.lastName}
        </div>
    )
}
