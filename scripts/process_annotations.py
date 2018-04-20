import json
import os


def is_good_annotation(annotation):
    in_tsuro_dir = os.path.dirname(annotation['path']) == 'tsuro'
    dunder = '__' in annotation['func_name']
    return in_tsuro_dir and not dunder


def is_good_type_comment(type_comment, annotation):
    param_type, return_type = type_comment.split(' -> ')
    has_arguments = param_type != '()'
    has_return = return_type != 'None'

    file_name, _ = os.path.splitext(os.path.basename(annotation['path']))
    defined_in_file = "{file_name}.".format(file_name=file_name) in type_comment

    return (has_return or has_arguments) and not defined_in_file


def process_annotations(annotations):
    clean_annotations = []
    for annotation in annotations:
        annotation['type_comments'] = [tc for tc in annotation['type_comments'] if is_good_type_comment(tc, annotation)]
        if is_good_annotation(annotation) and annotation['type_comments']:
            clean_annotations.append(annotation)
    return clean_annotations


def main(infile, outfile):
    if not os.path.exists(infile) or not infile.endswith('.json'):
        raise ValueError('Input must be a .json of type annotations created by pyannotate.')

    with open(infile, 'r') as fp:
        contents = fp.read()

    annotations = json.loads(contents)
    clean_annotations = process_annotations(annotations)

    print('Keeping {}/{} annotations.'.format(len(clean_annotations), len(annotations)))

    with open(outfile, 'w') as fp:
        json.dump(clean_annotations, fp=fp, indent=4)


if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("infile", help="Path to the annotations file.")
    parser.add_argument("outfile", help="Path to the cleaned annotations file.")
    args = parser.parse_args()
    args = vars(args)

    main(args['infile'], args['outfile'])
